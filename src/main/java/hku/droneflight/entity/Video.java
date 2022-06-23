package hku.droneflight.entity;

import hku.droneflight.util.VideoReq;
import java.util.Date;
import lombok.Data;
import lombok.ToString;

/**
 * @author hqj
 * @create 2022-06-15 23:00
 * video类
 * 暂定
 * 直接调uid
 */

@Data
@ToString
public class Video {
    private Integer id;
    private Integer uid;
    private String name;
    private String description;
    private String url;
    private long createTime;

    public Video(VideoReq videoReq) {
        this.uid = videoReq.uid;
        this.name = videoReq.name;
        this.description = videoReq.description;
        this.createTime = new Date().getTime();
    }
}
