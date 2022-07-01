package hku.droneflight.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import hku.droneflight.entity.User;
import hku.droneflight.entity.Video;
import hku.droneflight.service.UserService;
import hku.droneflight.service.VideoService;
import hku.droneflight.util.*;

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
    static Map<String, Integer> streamVideoMap = new HashMap<>();
    static Map<String, String> streamResultMap = new HashMap<>();
    static Map<String, User> streamUserMap = new HashMap<>();

    @Autowired
    UserService userService;

    @Autowired
    VideoService videoService;

    @Value("${result.url}")
    private String urlBasePath;

    /**
     * 开始直播接口，接收app发送的视频信息和推流地址
     *
     * @param liveReq
     */
    @RequestMapping(value = "/startLive")
    @ResponseBody
    ResponseMsg startLive(@RequestBody LiveReq liveReq) {
        streamUrls.add(liveReq.streamUrl);
        Integer userId = liveReq.user.getId();
        User user = userService.getUserById(userId);
        streamUserMap.put(liveReq.streamUrl, user);
        return new ResponseMsg(Result.SUCCESS);
    }

    /**
     * 查询是否有直播，返回第一个streamUrl和resultUrl
     * Result.FAIL表示没有直播
     *
     * @return
     */
    @RequestMapping(value = "/isLiveStart")
    @ResponseBody
    UrlRsp isLiveStart() {
        if (streamUrls.isEmpty()) {
            return new UrlRsp(Result.FAIL, "no live!");
        } else if (streamResultMap.containsKey(streamUrls.get(0))) {
            UrlRsp urlRsp = new UrlRsp(Result.SUCCESS);
            urlRsp.streamUrl = streamUrls.get(0);
            urlRsp.resultUrl = streamResultMap.get(urlRsp.streamUrl);
            return urlRsp;
        } else {
            UrlRsp urlRsp = new UrlRsp(Result.SUCCESS);
            urlRsp.streamUrl = streamUrls.get(0);
            User user = streamUserMap.get(urlRsp.streamUrl);
            if (user == null) {
                return new UrlRsp(Result.FAIL, "the user has no live stream now");
            }
            //按照原有的rtmp域名来更改后缀，生成新的rtmp url
            String[] split = urlRsp.streamUrl.split("/");
            split[split.length - 1] = "stream-" + urlRsp.streamUrl.hashCode();
            StringBuffer sb = new StringBuffer();
            for (String s : split) {
                sb.append(s);
                sb.append("/");
            }
            urlRsp.resultUrl = sb.substring(0, sb.length() - 2);
            streamResultMap.put(urlRsp.streamUrl, urlRsp.resultUrl);
            return urlRsp;
        }
    }

    /**
     * 停止直播，不保存信息
     *
     * @param liveReq
     * @return
     */
    @RequestMapping(value = "/stopLive")
    @ResponseBody
    ResponseMsg stopLive(@RequestBody LiveReq liveReq) {
        //加个校验
        streamUrls.remove(liveReq.streamUrl);
        streamResultMap.remove(liveReq.streamUrl);
        streamUserMap.remove(liveReq.streamUrl);
        return new ResponseMsg(Result.SUCCESS);
    }

    /**
     * 存储视频信息和删除streamUrl，记录vid
     *
     * @param videoReq
     * @return
     */
    @RequestMapping(value = "/stopAndSaveLive")
    @ResponseBody
    ResponseMsg stopAndSaveLive(@RequestBody VideoReq videoReq) {
        if (videoReq.streamUrl != null) {
            streamUrls.remove(videoReq.streamUrl);
            streamResultMap.remove(videoReq.streamUrl);
            User user = streamUserMap.remove(videoReq.streamUrl);
            if (user == null) {
                ResponseMsg fail = new ResponseMsg(Result.FAIL);
                fail.setFailReason("need input uid");
                return fail;
            }
            Video video = new Video(videoReq);
            video.setUid(user.getId());
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
     *
     * @return
     */
    @RequestMapping(value = "/isLiveStop")
    @ResponseBody
    LiveListRsp isLiveStop(@RequestBody LiveReq liveReq) {
        if (streamUrls.contains(liveReq.streamUrl))
            return new LiveListRsp(Result.FAIL);
        else
            return new LiveListRsp(Result.SUCCESS);
    }

    /**
     * 获取所有直播url
     *
     * @return
     */
    //加个list UrlRsp
    //加个user
    @RequestMapping(value = "/getLive")
    @ResponseBody
    UrlListRsp getLiveUrlList() {
        List<UrlRsp> urlRsps = new ArrayList<>();
        for (String streamUrl : streamUrls) {
            UrlRsp urlRsp = new UrlRsp(Result.SUCCESS);
            urlRsp.streamUrl = streamUrl;
            urlRsp.resultUrl = streamResultMap.get(streamUrl);
            urlRsp.user = streamUserMap.get(streamUrl);
            urlRsps.add(urlRsp);
        }
        UrlListRsp urlListRsp = new UrlListRsp(Result.SUCCESS);
        urlListRsp.urlRspList = urlRsps;
        return urlListRsp;
    }

    /**
     * 获取所有本地视频
     *
     * @return
     */
    @RequestMapping(value = "/getVideo")
    @ResponseBody
    VideoListRsp getRecordUrlList(Integer uid) {
        List<Video> videos = videoService.getListByUid(uid);
        if (videos.size() > 0) {
            VideoListRsp videoListRsp = new VideoListRsp(Result.SUCCESS);
            videoListRsp.videoList = videos;
            videoListRsp.user=userService.getUserById(uid);
            return videoListRsp;
        } else {
            return new VideoListRsp(Result.FAIL, "没有本地视频");
        }
    }
//    /**
//     * 根据url播放指定视频
//     * @return
//     */
//    @RequestMapping(value = "/playRecord")
//    @ResponseBody
//    VideoRsp playRecordVideo(String url){
//        LambdaQueryWrapper<Video> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(Video::getUrl,url);
//        Video video = videoService.getOne(queryWrapper);
//        if(video==null){
//            return new VideoRsp(Result.FAIL,"链接地址无效！");
//        }
//        else{
//            VideoRsp videoRsp= new VideoRsp(Result.SUCCESS);
//            videoRsp.video=video;
//            return videoRsp;
//        }
//    }
}
