package hku.picshare.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @Author hqj
 * 用户实体类
 */


@Data
@ToString
@Getter
@Setter
public class User implements Serializable {

    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
    private String email;
    private String description;
    @JsonProperty("password")
    private String pwd;
    private String avatar;

    public User(String name, String email, String description, String pwd, String avatar) {
        this.name = name;
        this.email = email;
        this.description = description;
        this.pwd = pwd;
        this.avatar = avatar;
    }
}
