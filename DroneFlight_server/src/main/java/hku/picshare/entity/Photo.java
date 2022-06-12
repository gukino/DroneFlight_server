package hku.picshare.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.ToString;

/**
 * @author yhp
 * @create 2022-04-20 23:00
 * photo类
 */
@Data
@ToString
public class Photo {
    private Integer id;
    @TableField(value = "form_id")
    private Integer formId;
    private String url;
}
