package pers.project.api.common.constant.rocketmq;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.support.RocketMQHeaders;

/**
 * RocketMQ 的 Spring 消息头名称前缀常量
 *
 * @author Luo Fei
 * @date 2023/06/30
 * @see RocketMQHeaders
 * @see MessageConst
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RocketMQSpringMessageHeaderNameConst {

    /**
     * RocketMQ 事务 ID
     */
    public static final String ROCKETMQ_TRANSACTION_ID = "rocketmq_TRANSACTION_ID";

}
