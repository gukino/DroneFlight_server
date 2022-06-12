package hku.picshare;

import hku.picshare.entity.User;
import hku.picshare.mapper.UserMapper;
import hku.picshare.service.UserService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceTest {

    @Autowired
    UserService userService;
    @Before
    public void before() throws Exception {
    }

    @After
    public void after() throws Exception {
    }

    @Test
    public void testAddUser() throws Exception {
        User user = new User("hqj2","375664705@qq.com","addtest","123","0");
        System.out.println(userService.newUser(user));
    }

    @Test
    public void testQueryUser() throws Exception {
        System.out.println(userService.getUserById(1).toString());
    }

}