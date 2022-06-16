package hku.droneflight.service;

import com.baomidou.mybatisplus.extension.service.IService;
import hku.droneflight.entity.Video;
import hku.droneflight.util.VideoReq;
import java.util.List;

public interface VideoService extends IService<Video>{
    List<Video> getListByUid(int uid);
    boolean updateVideo(VideoReq videoReq);
    boolean addVideo(VideoReq videoReq);
}
