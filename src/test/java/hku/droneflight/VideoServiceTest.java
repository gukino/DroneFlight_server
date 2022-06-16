package hku.droneflight;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import hku.droneflight.entity.User;
import hku.droneflight.entity.Video;
import hku.droneflight.mapper.VideoMapper;
import hku.droneflight.service.UserService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class VideoServiceTest {

    @Autowired
    VideoMapper videoMapper;
    @Before
    public void before() throws Exception {
    }

    @After
    public void after() throws Exception {
    }

    @Test
    public void testQueryUser() throws Exception {
        System.out.println(videoMapper.selectList(new QueryWrapper<Video>().lambda().eq(Video::getUid,1)).toString());
    }

}