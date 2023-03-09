package pers.project.api.security.model.dto.user_api_info;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;

/**
 * 更新请求
 *
 * @TableName product
 */
@Data
public class UserApiInfoUpdateRequest implements Serializable {

    /**
     * 主键
     */
    private Long id;

    /**
     * 总调用次数
     */
    private Integer totalNum;

    /**
     * 剩余调用次数
     */
    private Integer leftNum;

    /**
     * 状态(0-正常，1-禁用)
     */
    private Integer status;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}