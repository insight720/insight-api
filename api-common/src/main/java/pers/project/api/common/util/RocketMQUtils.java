package pers.project.api.common.util;

import org.springframework.messaging.Message;
import org.springframework.util.Assert;

import static org.apache.rocketmq.spring.support.RocketMQHeaders.PREFIX;
import static org.apache.rocketmq.spring.support.RocketMQHeaders.TRANSACTION_ID;

/**
 * RocketMQ 工具类
 *
 * @author Luo Fei
 * @date 2023/06/30
 */
public abstract class RocketMQUtils {

    /**
     * 从给定的 Spring 消息中获取 RocketMQ 消息事务 ID。
     *
     * @param message 包含事务 ID 的 Spring 消息。
     * @return 获取到的事务 ID。
     */
    public static String getTransactionId(Message<?> message) {
        String transactionId = message.getHeaders().get(PREFIX + TRANSACTION_ID, String.class);
        Assert.notNull(transactionId, "The transaction ID must be not null");
        return transactionId;
    }

}
