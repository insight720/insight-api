package pers.project.api.common.constant.rocketmq;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * RocketMQ 消息标签名称常量
 * <p>
 * 消息标签命名规则参考：
 * <pre>
 * stock_deduction_tag
 * 业务名_tag
 * </pre>
 *
 * @author Luo Fei
 * @date 2023/06/27
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RocketMQTagNameConst {

    /**
     * 接口计数用法存量扣减标签
     */
    public static final String QUANTITY_USAGE_STOCK_DEDUCTION_TAG
            = "quantity_usage_stock_deduction_tag";

    /**
     * 接口计数用法订单状态更新标签
     */
    public static final String QUANTITY_USAGE_ORDER_STATUS_UPDATE_TAG
            = "quantity_usage_order_status_update_tag";

}
