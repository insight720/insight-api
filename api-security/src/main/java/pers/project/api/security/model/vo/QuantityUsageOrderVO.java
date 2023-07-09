package pers.project.api.security.model.vo;

import lombok.Data;
import pers.project.api.common.validation.constraint.SnowflakeId;

import java.time.LocalDateTime;

/**
 * 接口计数用法订单 VO
 *
 * @author Luo Fei
 * @date 2023/07/08
 */
@Data
public class QuantityUsageOrderVO {

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
     * 下单的调用次数
     */
    private String quantity;

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
