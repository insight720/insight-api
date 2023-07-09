package pers.project.api.common.model.dto;

import lombok.Data;

/**
 * 接口计数用法存量释放 DTO
 *
 * @author Luo Fei
 * @date 2023/07/03
 */
@Data
public class QuantityUsageStockReleaseDTO {

    /**
     * 订单号
     */
    private String orderSn;

    /**
     * 接口摘要主键
     */
    private String digestId;

    /**
     * 下单的调用次数
     */
    private String quantity;

}
