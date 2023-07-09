package pers.project.api.common.model.dto;

import lombok.Data;

/**
 * 接口计数用法存量确认 DTO
 *
 * @author Luo Fei
 * @date 2023/07/06
 */
@Data
public class QuantityUsageStockConfirmationDTO {

    /**
     * 订单号
     */
    private String orderSn;

    /**
     * 用户接口计数用法主键
     */
    private String usageId;

    /**
     * 确认的调用次数存量
     */
    private String quantity;

}
