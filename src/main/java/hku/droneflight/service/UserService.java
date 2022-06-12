package hku.droneflight.service;

import com.baomidou.mybatisplus.extension.service.IService;
import hku.droneflight.entity.User;
import hku.droneflight.util.ResponseMsg;

/**
 * @author yhp
 *
 */
public interface UserService extends IService<User> {
    ResponseMsg newUser(User user);
    ResponseMsg login(String email, String pwd);
    User getUserById(int id);
}
