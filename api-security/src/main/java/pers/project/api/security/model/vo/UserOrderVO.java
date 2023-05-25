package pers.project.api.security.model.vo;

import lombok.Data;
import pers.project.api.common.validation.constraint.SnowflakeId;

import java.time.LocalDateTime;

/**
 * 用户订单 VO
 *
 * @author Luo Fei
 * @date 2023/05/16
 */
@Data
public class UserOrderVO {

    /**
     * 订单主键
     */
    @SnowflakeId
    private String orderId;

    /**
     * 订单编号
     */
    private String orderSn;

    /**
     * 订单描述
     */
    private String description;

    /**
     * 接口摘要主键
     */
    private String digestId;

    /**
     * 用户接口用法主键
     */
    private String usageId;

    /**
     * 接口用法类型
     */
    private String usageType;

    /**
     * 订单状态
     */
    private Integer orderStatus;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

}
