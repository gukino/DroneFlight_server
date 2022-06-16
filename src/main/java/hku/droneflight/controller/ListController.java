package hku.droneflight.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import hku.droneflight.entity.Video;
import hku.droneflight.mapper.VideoMapper;
import hku.droneflight.service.VideoService;
import hku.droneflight.util.RequestMsg;
import hku.droneflight.util.ResponseMsg;
import hku.droneflight.util.Result;
import hku.droneflight.util.VideoListRsp;
import hku.droneflight.util.VideoReq;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *  查询用户的所有视频列表
 */

@RestController
public class ListController {
    @Autowired
    VideoMapper videoMapper;
    @Autowired
    VideoService videoService;

    @RequestMapping(value = "/getList")
    @ResponseBody
    public VideoListRsp getList(@RequestBody RequestMsg requestMsg){
        List<Video> videoList = videoService.getListByUid(requestMsg.user.getId());
        VideoListRsp rsp = new VideoListRsp(Result.SUCCESS);
        rsp.videoList = videoList;
        return rsp;
    }

    @RequestMapping(value = "/addVideo")
    @ResponseBody
    public ResponseMsg addVideo(@RequestBody VideoReq videoReq){
        if (videoService.addVideo(videoReq)){
            return new ResponseMsg(Result.SUCCESS);
        }
        return new ResponseMsg(Result.FAIL);
    }

    @RequestMapping(value = "/updateVideo")
    @ResponseBody
    public ResponseMsg updateVideo(@RequestBody VideoReq videoReq){
        if (videoService.updateVideo(videoReq)){
            return new ResponseMsg(Result.SUCCESS);
        }
        return new ResponseMsg(Result.FAIL);
    }

    @RequestMapping(value = "/deleteVideo")
    @ResponseBody
    public VideoListRsp deleteVideo(@RequestBody VideoReq videoReq){
        videoMapper.deleteById(videoReq.id);
        return new VideoListRsp(Result.SUCCESS);
    }

}
