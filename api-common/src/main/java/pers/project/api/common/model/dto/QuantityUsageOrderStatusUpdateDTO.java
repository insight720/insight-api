package pers.project.api.common.model.dto;

import lombok.Data;
import pers.project.api.common.enumeration.QuantityUsageOrderStatusEnum;

/**
 * 接口计数用法状态更新 DTO
 *
 * @author Luo Fei
 * @date 2023/06/28
 */
@Data
public class QuantityUsageOrderStatusUpdateDTO {

    /**
     * 订单号
     */
    private String orderSn;

    /**
     * 用户接口用法主键
     */
    private String usageId;

    /**
     * 订单状态
     *
     * @see QuantityUsageOrderStatusEnum#storedValue()
     */
    private Integer orderStatus;

}
