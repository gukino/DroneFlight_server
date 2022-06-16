package hku.droneflight.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import hku.droneflight.entity.Video;
import hku.droneflight.mapper.VideoMapper;
import hku.droneflight.service.VideoService;
import hku.droneflight.util.VideoReq;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author yhp
 *
 */
@Service
public class VideoServiceImpl extends ServiceImpl<VideoMapper, Video> implements VideoService {

    @Autowired
    VideoMapper videoMapper;

    @Override
    public List<Video> getListByUid(int uid) {
        return videoMapper.selectList(new QueryWrapper<Video>().lambda().eq(Video::getUid, uid));
    }

    @Override
    public boolean updateVideo(VideoReq videoReq) {
        Video video = videoMapper.selectById(videoReq.id);
        video.setDescription(videoReq.description);
        video.setName(videoReq.name);
        videoMapper.updateById(video);
        return true;
    }

    @Override
    public boolean addVideo(VideoReq videoReq) {
        //TODO
        return true;
    }
}
