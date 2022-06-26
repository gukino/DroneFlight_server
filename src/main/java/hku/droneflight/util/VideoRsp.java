package hku.droneflight.util;

import hku.droneflight.entity.Video;

import java.util.List;

/**
 * @author yhp
 * @create 2022-06-26 14:44
 */
public class VideoRsp extends ResponseMsg{

    public Video video;
    public VideoRsp(Result result) {
        super(result);
    }

    public VideoRsp(Result result, String reason) {
        super(result, reason);
    }
}
