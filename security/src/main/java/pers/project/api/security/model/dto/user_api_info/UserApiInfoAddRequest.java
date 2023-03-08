package pers.project.api.security.model.dto.user_api_info;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;

/**
 * 创建请求
 *
 * @TableName product
 */
@Data
public class UserApiInfoAddRequest implements Serializable {

    /**
     * 调用用户 id
     */
    private Long userId;

    /**
     * 接口 id
     */
    private Long apiInfoId;

    /**
     * 总调用次数
     */
    private Integer totalNum;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}