package hku.droneflight.controller;

import hku.droneflight.entity.Video;
import hku.droneflight.service.VideoService;
import hku.droneflight.util.LiveListRsp;
import hku.droneflight.util.LiveReq;
import hku.droneflight.util.ResponseMsg;
import hku.droneflight.util.Result;
import hku.droneflight.util.UrlRsp;
import hku.droneflight.util.VideoReq;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 */

@Controller
public class LiveController {

    private List<String> streamUrls;
    private List<String> resultUrls;
    private Map<String,Integer> streamVideoMap;

    @Autowired
    VideoService videoService;

    /**
     * 开始直播接口，接收app发送的视频信息和推流地址
     * @param liveReq
     */
    @RequestMapping(value = "/startLive")
    @ResponseBody
    ResponseMsg startLive(LiveReq liveReq){
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
            urlRsp.url =  streamUrls.get(0);
            streamUrls.remove(0);
            return urlRsp;
        }
    }

    /**
     * 停止直播，不保存信息
     * @param streamUrl
     * @return
     */
    @RequestMapping(value = "/stopLive")
    @ResponseBody
    ResponseMsg stopLive(String streamUrl){
        streamUrls.remove(streamUrl);
        return new ResponseMsg(Result.SUCCESS);
    }

    /**
     * 存储视频信息和删除streamUrl，记录vid
     * @param videoReq
     * @return
     */
    @RequestMapping(value = "/stopAndSaveLive")
    @ResponseBody
    ResponseMsg stopAndSaveLive(VideoReq videoReq){
        if (videoReq.streamUrl != null){
            streamUrls.remove(videoReq.streamUrl);

            int vId = videoService.addVideo(new Video(videoReq));
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
    LiveListRsp isLiveStop(String streamUrl){

        for (String urls : streamUrls){
            if (streamUrl.equals(urls)){
                return new LiveListRsp(Result.FAIL);
            }
        }
        return new LiveListRsp(Result.SUCCESS);
    }


}
