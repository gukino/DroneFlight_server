package hku.droneflight.util;

import hku.droneflight.entity.Video;
import java.util.List;

/**
 * 返回视频列表类
 */

public class VideoListRsp extends ResponseMsg {
    public List<Video> videoList;

    public VideoListRsp(Result result) {
        super(result);
    }

    public VideoListRsp(Result result, String reason) {
        super(result, reason);
    }

}
