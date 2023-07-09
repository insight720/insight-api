package pers.project.api.common.model.query;

import lombok.Data;

/**
 * 接口计数用法订单接口 Query
 *
 * @author Luo Fei
 * @date 2023/07/09
 */
@Data
public class QuantityUsageOrderApiQuery {

    // region Same with QuantityUsageOrderPO
    /**
     * 接口摘要主键
     */
    private String digestId;

    /**
     * 用户接口用法主键
     */
    private String usageId;
    // endregion

}
