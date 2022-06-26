package hku.droneflight.controller;

import hku.droneflight.entity.Video;
import hku.droneflight.service.VideoService;
import hku.droneflight.util.LiveListRsp;
import hku.droneflight.util.LiveReq;
import hku.droneflight.util.ResponseMsg;
import hku.droneflight.util.Result;
import hku.droneflight.util.UrlRsp;
import hku.droneflight.util.VideoReq;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 */

@Controller
public class LiveController {

    static List<String> streamUrls = new ArrayList<>();
    static Map<String,Integer> streamVideoMap = new HashMap<>();
    static Map<String,Integer> streamUidMap = new HashMap<>();

    @Autowired
    VideoService videoService;

    @Value("${result.url}")
    private String urlBasePath;

    /**
     * 开始直播接口，接收app发送的视频信息和推流地址
     * @param liveReq
     */
    @RequestMapping(value = "/startLive")
    @ResponseBody
    ResponseMsg startLive(@RequestBody LiveReq liveReq){
        streamUrls.add(liveReq.streamUrl);
        return new ResponseMsg(Result.SUCCESS);
    }

    /**
     * 查询是否有直播，返回streamUrls列表
     * Result.FAIL表示没有直播
     * @return
     */
    @RequestMapping(value = "/isLiveStart")
    @ResponseBody
    UrlRsp isLiveStart(){

        if (streamUrls.isEmpty()){
            return new UrlRsp(Result.FAIL);
        }else{
            UrlRsp urlRsp = new UrlRsp(Result.SUCCESS);
            urlRsp.streamUrl =  streamUrls.get(0);
            urlRsp.resultUrl =  urlBasePath+ UUID.randomUUID();
            streamUrls.remove(0);
            return urlRsp;
        }
    }

    /**
     * 停止直播，不保存信息
     * @param liveReq
     * @return
     */
    @RequestMapping(value = "/stopLive")
    @ResponseBody
    ResponseMsg stopLive(@RequestBody LiveReq liveReq){
        streamUrls.remove(liveReq.streamUrl);
        return new ResponseMsg(Result.SUCCESS);
    }

    /**
     * 存储视频信息和删除streamUrl，记录vid
     * @param videoReq
     * @return
     */
    @RequestMapping(value = "/stopAndSaveLive")
    @ResponseBody
    ResponseMsg stopAndSaveLive(@RequestBody VideoReq videoReq){
        if (videoReq.streamUrl != null){
            streamUrls.remove(videoReq.streamUrl);
            Video video = new Video(videoReq);
            videoService.addVideo(video);
            Integer vId = video.getId();
            streamVideoMap.put(videoReq.streamUrl, vId);
            return new ResponseMsg(Result.SUCCESS);
        }
        return new ResponseMsg(Result.FAIL);
    }


    /**
     * 查询指定streamUrl直播是否停止
     * Result.FAIL表示该url仍在直播
     * @param streamUrl
     * @return
     */
    @RequestMapping(value = "/isLiveStop")
    @ResponseBody
    LiveListRsp isLiveStop(@RequestBody String streamUrl){
        if(streamUrls.contains(streamUrl))
            return new LiveListRsp(Result.FAIL);
        else
            return new LiveListRsp(Result.SUCCESS);
    }


}
