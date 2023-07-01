package pers.project.api.common.constant.rocketmq;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * RocketMQ 主题名称常量
 * <p>
 * 主题命名规则参考：
 * <pre>
 *     security_quantity_usage_order_transaction_topic
 *     业务域所属的项目_业务域_消息类型_topic
 * </pre>
 *
 * @author Luo Fei
 * @date 2023/06/26
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RocketMQTopicNameConst {

    /**
     * Security 项目的接口计数用法订单事务主题
     */
    public static final String SECURITY_QUANTITY_USAGE_ORDER_TRANSACTION_TOPIC
            = "security_quantity_usage_order_transaction_topic";

    /**
     * Security 项目的接口计数用法订单普通主题
     */
    public static final String SECURITY_QUANTITY_USAGE_ORDER_NORMAL_TOPIC
            = "security_quantity_usage_order_normal_topic";

    /**
     * Facade 项目的接口计数用法事务主题
     */
    public static final String FACADE_QUANTITY_USAGE_TRANSACTION_TOPIC
            = "facade_quantity_usage_transaction_topic";

}
