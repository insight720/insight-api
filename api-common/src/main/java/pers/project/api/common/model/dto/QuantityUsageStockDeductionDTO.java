package pers.project.api.common.model.dto;

import lombok.Data;

/**
 * 接口计数用法存量扣减 DTO
 *
 * @author Luo Fei
 * @date 2023/06/26
 */
@Data
public class QuantityUsageStockDeductionDTO {

    /**
     * 用户账户主键
     */
    private String accountId;

    /**
     * 接口摘要主键
     */
    private String digestId;

    /**
     * 订单锁定的调用次数
     */
    private String orderQuantity;

    /**
     * 订单编号
     */
    private String orderSn;

}
