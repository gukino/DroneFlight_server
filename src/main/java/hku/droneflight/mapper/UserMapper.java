package hku.droneflight.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import hku.droneflight.entity.User;
import org.springframework.stereotype.Repository;

/**
 * @author yhp
 *
 */

/**
 * 只需要我们的Mapper继承 BaseMapper 就可以拥有crud能力
 */



public interface UserMapper extends BaseMapper<User> {


}
