package hku.picshare.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import hku.picshare.entity.User;
import hku.picshare.mapper.UserMapper;
import hku.picshare.service.UserService;
import hku.picshare.util.ResponseMsg;
import hku.picshare.util.Result;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author yhp
 *
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    UserMapper userMapper;

    @Override
    public ResponseMsg newUser(User user) {
        Map<String, Object> map = new HashMap<>();
        map.put("email", user.getEmail());
        List<User> list = userMapper.selectByMap(map);
        if (!list.isEmpty()) {
            return new ResponseMsg(Result.FAIL, "email already exist");
        }
        int ret = userMapper.insert(user);
        if (ret != 1){
            return new ResponseMsg(Result.FAIL,"sql fail");
        }
        return new ResponseMsg(Result.SUCCESS);
    }

    @Override
    public ResponseMsg login(String email, String pwd) {
        Map<String, Object> map = new HashMap<>();
        map.put("email", email);
        List<User> list = userMapper.selectByMap(map);
        if (list.size() > 0){
            for (User user : list){
                if (user.getPwd().equals(pwd)){
                    ResponseMsg ret = new ResponseMsg(Result.SUCCESS);
                    ret.user = user;
                    return ret;
                }
            }
            return new ResponseMsg(Result.FAIL,"password not correct.");
        }
        return new ResponseMsg(Result.FAIL,"no such email.");
    }

    @Override
    public User getUserById(int id) {
        return userMapper.selectById(id);
    }
}
