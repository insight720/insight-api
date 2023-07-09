package pers.project.api.common.constant.rocketmq;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * RocketMQ 消费者组名称常量
 * <p>
 * 消费者组命名规则参考：
 * <pre>
 * facade_quantity_usage_stock_deduction_group
 * 消费者所属的项目_业务名_group
 * </pre>
 *
 * @author Luo Fei
 * @date 2023/06/26
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RocketMQConsumerGroupNameConst {

    /**
     * Facade 项目的接口计数用法存量扣减组
     */
    public static final String FACADE_QUANTITY_USAGE_STOCK_DEDUCTION_GROUP
            = "facade_quantity_usage_stock_deduction_group";

    /**
     * Facade 项目的接口计数用法存量释放组
     */
    public static final String FACADE_QUANTITY_USAGE_STOCK_RELEASE_GROUP
            = "facade_quantity_usage_stock_release_group";

    /**
     * Facade 项目的接口计数用法存量确认组
     */
    public static final String FACADE_QUANTITY_USAGE_STOCK_CONFIRMATION_GROUP
            = "facade_quantity_usage_stock_confirmation_group";

    /**
     * Security 项目的接口计数用法订单状态更新组
     */
    public static final String SECURITY_QUANTITY_USAGE_ORDER_STATUS_UPDATE_GROUP
            = "security_quantity_usage_order_status_update_group";

    /**
     * Security 项目的接口计数用法订单定时关闭组
     */
    public static final String QUANTITY_USAGE_ORDER_SCHEDULED_CLOSE_GROUP
            = "quantity_usage_order_scheduled_close_group";

}
