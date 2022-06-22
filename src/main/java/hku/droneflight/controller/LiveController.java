package hku.droneflight.controller;

import hku.droneflight.util.LiveListRsp;
import hku.droneflight.util.LiveReq;
import hku.droneflight.util.ResponseMsg;
import hku.droneflight.util.Result;
import hku.droneflight.util.VideoListRsp;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 */

@Controller
public class LiveController {

    private List<String> streamUrls;
    private List<String> resultUrls;

    /**
     * 开始直播接口，接收app发送的视频信息和推流地址
     * @param liveReq
     */
    @RequestMapping(value = "/startLive")
    @ResponseBody
    ResponseMsg startLive(String liveReq){
        streamUrls.add(liveReq);

        return new ResponseMsg(Result.SUCCESS);
    }

    /**
     * Result.FAIL表示没有直播
     * @return
     */
    @RequestMapping(value = "/isLiveStart")
    @ResponseBody
    LiveListRsp isLiveStart(){

        if (streamUrls.isEmpty()){
            return new LiveListRsp(Result.FAIL);
        }else{
            LiveListRsp liveListRsp = new LiveListRsp(Result.SUCCESS);
            liveListRsp.streamUrls = streamUrls;
            return liveListRsp;
        }
    }

}
