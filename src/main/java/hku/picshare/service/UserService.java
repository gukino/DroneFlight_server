package hku.picshare.service;

import com.baomidou.mybatisplus.extension.service.IService;
import hku.picshare.entity.User;
import hku.picshare.util.ResponseMsg;

/**
 * @author yhp
 *
 */
public interface UserService extends IService<User> {
    ResponseMsg newUser(User user);
    ResponseMsg login(String email, String pwd);
    User getUserById(int id);
}
