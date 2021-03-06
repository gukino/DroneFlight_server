package hku.droneflight.controller;

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


    static class Stream{
//        String streamUrl;
        String resultUrl;
        Integer VideoId;
        Integer UserId;
//        String serverId;

        public Stream() {
        }
    }
    //当前所有的stream
    static List<String> streamUrls = new ArrayList<>();
    //尚未分配的stream
    static Queue<String> availableStreams = new ArrayDeque<>();

    //当前所有的stream的map
    static Map<String, Stream> streamMap = new HashMap<>();
    //server与stream的map
    static Map<String, String> serverStreamMap = new HashMap<>();
    //已经停止，但等待上传视频的stream map. key:streamUrl, value:Stream
    static Map<String, Stream> stoppedStreamMap = new HashMap<>();

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
        if (liveReq.streamUrl == null || liveReq.streamUrl.length() <= 0) {
            return new ResponseMsg(Result.FAIL, "no stream url");
        }
        if (liveReq.user == null || liveReq.user.getId() == null || liveReq.user.getId() <= 0) {
            return new ResponseMsg(Result.FAIL, "no uid");
        }
        if (streamUrls.contains(liveReq.streamUrl)) {
            Stream stream = streamMap.get(liveReq.streamUrl);
            if (Objects.equals(stream.UserId, liveReq.user.getId())) {
                //已经启动过的，那就直接返回成功
                return new ResponseMsg(Result.SUCCESS);
            } else {
                return new ResponseMsg(Result.FAIL, "stream url:" + liveReq.streamUrl + "has been used");
            }
        }
        streamUrls.add(liveReq.streamUrl);
        availableStreams.add(liveReq.streamUrl);
        Integer userId = liveReq.user.getId();
        Stream stream= new Stream();
        stream.UserId = userId;
        streamMap.put(liveReq.streamUrl, stream);
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
    UrlRsp isLiveStart(@RequestBody ServerReq serverReq) {
        if (serverReq.serverId == null || serverReq.serverId.length() <=0) {
            return new UrlRsp(Result.FAIL, "no server id");
        }
        if (serverStreamMap.containsKey(serverReq.serverId)) {
            UrlRsp urlRsp = new UrlRsp(Result.SUCCESS);
            urlRsp.streamUrl = serverStreamMap.get(serverReq.serverId);
            urlRsp.resultUrl = streamMap.get(urlRsp.streamUrl).resultUrl;
            return urlRsp;
        } else {
            if (availableStreams.size()>0){
                UrlRsp urlRsp = new UrlRsp(Result.SUCCESS);
                urlRsp.streamUrl = availableStreams.peek();
                availableStreams.poll();
                serverStreamMap.put(serverReq.serverId,urlRsp.streamUrl);

                if (streamMap.get(urlRsp.streamUrl).UserId == null) {
                    return new UrlRsp(Result.FAIL, "the user has no live stream now");
                }

                //按照原有的rtmp域名来更改后缀，生成新的rtmp url
                String[] split = urlRsp.streamUrl.split("/");
                split[split.length - 1] = "stream_" + Math.abs(urlRsp.streamUrl.hashCode());
                StringBuffer sb = new StringBuffer();
                for (String s : split) {
                    sb.append(s);
                    sb.append("/");
                }
                urlRsp.resultUrl = sb.substring(0, sb.length() - 2);
                Stream stream = streamMap.get(urlRsp.streamUrl);
                stream.resultUrl = urlRsp.resultUrl;
                streamMap.put(urlRsp.streamUrl, stream);
                return urlRsp;
            }
            return new UrlRsp(Result.FAIL, "no live!");
        }
    }

//    @RequestMapping(value = "/isLiveStart/all")
//    @ResponseBody
//    UrlRsp isLiveStartAll() {
//        if (streamUrls.isEmpty()) {
//            return new UrlRsp(Result.FAIL, "no live!");
//        } else if (streamResultMap.containsKey(streamUrls.get(0))) {
//            UrlRsp urlRsp = new UrlRsp(Result.SUCCESS);
//            urlRsp.streamUrl = streamUrls.get(0);
//            urlRsp.resultUrl = streamResultMap.get(urlRsp.streamUrl);
//            return urlRsp;
//        } else {
//            UrlRsp urlRsp = new UrlRsp(Result.SUCCESS);
//            urlRsp.streamUrl = streamUrls.get(0);
//            User user = streamUserMap.get(urlRsp.streamUrl);
//            if (user == null) {
//                return new UrlRsp(Result.FAIL, "the user has no live stream now");
//            }
//            //按照原有的rtmp域名来更改后缀，生成新的rtmp url
//            String[] split = urlRsp.streamUrl.split("/");
//            split[split.length - 1] = "stream-" + urlRsp.streamUrl.hashCode();
//            StringBuffer sb = new StringBuffer();
//            for (String s : split) {
//                sb.append(s);
//                sb.append("/");
//            }
//            urlRsp.resultUrl = sb.substring(0, sb.length() - 2);
//            streamResultMap.put(urlRsp.streamUrl, urlRsp.resultUrl);
//            return urlRsp;
//        }
//    }

    /**
     * 停止直播，不保存信息
     *
     * @param liveReq
     * @return
     */
    @RequestMapping(value = "/stopLive")
    @ResponseBody
    ResponseMsg stopLive(@RequestBody LiveReq liveReq) {
        if (liveReq.streamUrl != null && streamUrls.contains(liveReq.streamUrl)) {
            return stopLiveInner(liveReq.streamUrl);
        } else {
            return new ResponseMsg(Result.FAIL, "no stream url");
        }
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
        if (videoReq.streamUrl != null && streamUrls.contains(videoReq.streamUrl)) {
            streamUrls.remove(videoReq.streamUrl);
            Stream stream = streamMap.get(videoReq.streamUrl);

            if (stream.UserId == null) {
                ResponseMsg fail = new ResponseMsg(Result.FAIL);
                fail.setFailReason("need input uid");
                return fail;
            }

            Video video = new Video(videoReq);
            video.setUid(stream.UserId);
            videoService.addVideo(video);
            stream.VideoId = video.getId();
            //清除所有相关这个stream url的记录。只保留上面的video数据库
            return stopLiveInner(videoReq.streamUrl);
        } else {
            return new ResponseMsg(Result.FAIL, "no stream url");
        }
    }

    private ResponseMsg stopLiveInner(String streamUrl) {
        streamUrls.remove(streamUrl);
        serverStreamMap.entrySet().removeIf(item -> Objects.equals(item.getValue(), streamUrl));
        Stream stoppedStream = streamMap.remove(streamUrl);
        stoppedStreamMap.put(streamUrl, stoppedStream);
        return new ResponseMsg(Result.SUCCESS);
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
     * 获取当前用户直播url
     *
     * @return
     */
    @RequestMapping(value = "/getLiveByUid")
    @ResponseBody
    UrlListRsp getLiveUrl(@RequestBody RequestMsg requestMsg) {
        List<Url> urls = new ArrayList<>();
        User user = userService.getUserById(requestMsg.user.getId());
        if (user == null) {
            //如果当前user不在数据库里，提前返回空list
            UrlListRsp urlListRsp = new UrlListRsp(Result.SUCCESS);
            urlListRsp.urlRspList = new ArrayList<>();
            return urlListRsp;
        }
        user.setPwd(null); //防止用户密码泄露
        for (String streamUrl : streamUrls) {
            Stream stream = streamMap.get(streamUrl);
            if (user.getId().equals(stream.UserId)){
                Url url = new Url(streamUrl, stream.resultUrl);
                url.user = user;
                urls.add(url);
            }
        }
        UrlListRsp urlListRsp = new UrlListRsp(Result.SUCCESS);
        urlListRsp.urlRspList = urls;
        return urlListRsp;
    }


    /**
     * 获取所有直播url
     *
     * @return
     */
    @RequestMapping(value = "/getLiveList")
    @ResponseBody
    UrlListRsp getLiveUrlList() {
        List<Url> urls = new ArrayList<>();
        for (String streamUrl : streamUrls) {
            Stream stream = streamMap.get(streamUrl);
            Url url = new Url(streamUrl, stream.resultUrl);
            url.user = userService.getUserById(stream.UserId);
            if (url.user != null) {
                url.user.setPwd(null); //防止用户密码泄露
            }
            urls.add(url);
        }
        UrlListRsp urlListRsp = new UrlListRsp(Result.SUCCESS);
        urlListRsp.urlRspList = urls;
        return urlListRsp;
    }

    /**
     * 获取所有本地视频
     *
     * @return
     */
    @RequestMapping(value = "/getVideo")
    @ResponseBody
    VideoListRsp getRecordUrlList(@RequestBody RequestMsg requestMsg) {
        List<Video> videos = null;
        if (requestMsg.user.getId() > 0) {
            videos = videoService.getListByUid(requestMsg.user.getId());
        } else {
            videos = videoService.list();
        }
        for (Video video:videos) {
            video.user = userService.getUserById(video.getUid());
            video.user.setPwd(null); //防止用户密码泄露
        }
        if (videos.size() > 0) {
            VideoListRsp videoListRsp = new VideoListRsp(Result.SUCCESS);
            videoListRsp.videoList = videos;
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
