package pers.project.api.security.message;

import com.alibaba.fastjson2.JSON;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.Assert;
import pers.project.api.common.util.RedisUtils;
import pers.project.api.common.util.TransactionUtils;
import pers.project.api.security.model.dto.QuantityUsageOrderScheduledCloseDTO;
import pers.project.api.security.service.QuantityUsageOrderService;

import java.util.function.Consumer;

import static pers.project.api.common.constant.redis.RedisKeyPrefixConst.QUANTITY_USAGE_ORDER_SCHEDULED_CLOSE_MESSAGE_KEYS_KEY_PREFIX;
import static pers.project.api.common.constant.redis.RedisKeyPrefixConst.QUANTITY_USAGE_STOCK_RELEASE_MESSAGE_KEYS_KEY_PREFIX;
import static pers.project.api.common.constant.rocketmq.RocketMQConsumerGroupNameConst.QUANTITY_USAGE_ORDER_SCHEDULED_CLOSE_GROUP;
import static pers.project.api.common.constant.rocketmq.RocketMQTagNameConst.QUANTITY_USAGE_ORDER_SCHEDULED_CLOSE_TAG;
import static pers.project.api.common.constant.rocketmq.RocketMQTopicNameConst.SECURITY_QUANTITY_USAGE_ORDER_DELAY_TOPIC;

/**
 * 接口计数用法订单定时关闭监听器
 *
 * @author Luo Fei
 * @date 2023/06/29
 */
@Slf4j
@Component
@RequiredArgsConstructor
@RocketMQMessageListener
        (topic = SECURITY_QUANTITY_USAGE_ORDER_DELAY_TOPIC,
                selectorExpression = QUANTITY_USAGE_ORDER_SCHEDULED_CLOSE_TAG,
                consumerGroup = QUANTITY_USAGE_ORDER_SCHEDULED_CLOSE_GROUP)
public class QuantityUsageOrderScheduledCloseListener implements RocketMQListener<MessageExt> {

    private final QuantityUsageOrderService quantityUsageOrderService;

    private final TransactionTemplate transactionTemplate;

    private final RedisTemplate<String, Object> redisTemplate;

    private final RedisTemplate<String, Object> redisTransactionTemplate;

    @Override
    public void onMessage(MessageExt orderScheduledCloseMessageExt) {
        // 获取消息体内容
        QuantityUsageOrderScheduledCloseDTO orderScheduledCloseDTO
                = JSON.parseObject(orderScheduledCloseMessageExt.getBody(), QuantityUsageOrderScheduledCloseDTO.class);
        String orderSn = orderScheduledCloseDTO.getOrderSn();
        // 检查作用于消费过程幂等的令牌
        String orderScheduledCloseMessageKeys = orderScheduledCloseMessageExt.getKeys();
        Assert.notNull(orderScheduledCloseMessageKeys, "The orderScheduledCloseMessageKeys must be not null");
        String orderScheduledCloseMessageKeysKey = QUANTITY_USAGE_ORDER_SCHEDULED_CLOSE_MESSAGE_KEYS_KEY_PREFIX + orderSn;
        boolean isIdempotent = RedisUtils.checkIdempotencyToken
                (redisTemplate, orderScheduledCloseMessageKeysKey, orderScheduledCloseMessageKeys);
        if (!isIdempotent) {
            log.info("Duplicate order scheduled close message, orderSn: {}", orderSn);
            return;
        }
        // 本地事务
        Consumer<Message<byte[]>> localTransactionExecutionConsumer
                // 回调的消息（用于打印日志等需求）
                = stockReleaseMessage -> transactionTemplate.executeWithoutResult(ignored -> {
            // 注册事务同步操作
            registerTransactionSynchronization
                    (orderSn, orderScheduledCloseMessageKeysKey, orderScheduledCloseMessageKeys);
            // 更新订单关闭状态
            quantityUsageOrderService.updateQuantityUsageOrderCloseStatus(orderScheduledCloseDTO);
            // 设置作用于存量释放消息消费过程幂等的令牌（Spring 管理的 Redis 事务）
            String orderStockReleaseMessageKeysKey = QUANTITY_USAGE_STOCK_RELEASE_MESSAGE_KEYS_KEY_PREFIX + orderSn;
            redisTransactionTemplate.opsForValue().set(orderStockReleaseMessageKeysKey, orderSn);
        });
        // 以下代码不在本地事务之内，如果抛出异常会产生消费重试
        try {
            boolean isSuccess = quantityUsageOrderService.checkWhetherOrderStatusIsSuccess(orderSn);
            if (isSuccess) {
                log.debug("Order has already been confirmed or cancelled, orderSn: {}", orderSn);
                return;
            }
            quantityUsageOrderService.sendStockReleaseTransactionMessage(orderSn, localTransactionExecutionConsumer);
        } catch (Exception e) {
            // 回滚作用于接口计数用法存量释放消息消费过程幂等的订单号以支持消费重试
            rollbackOrderScheduledCloseMessageKeys(orderScheduledCloseMessageKeysKey, orderScheduledCloseMessageKeys);
            throw e;
        }
    }

    /**
     * 注册事务同步操作。
     * <p>
     * 这个方法在事务完成后根据事务状态执行同步操作：
     * <pre>
     * 1. 提交：暂无操作。
     * 2. 回滚：回滚作用于接口计数用法订单定时关闭延时消息消费过程幂等的令牌。
     * 3. 未知：打印日志，人工处理。
     * </pre>
     *
     * @param orderSn                           订单号
     * @param orderScheduledCloseMessageKeysKey 接口计数用法订单定时关闭延时消息 KEYS 键
     * @param orderScheduledCloseMessageKeys    接口计数用法订单定时关闭延时消息 KEYS 值
     */
    private void registerTransactionSynchronization(String orderSn,
                                                    String orderScheduledCloseMessageKeysKey,
                                                    String orderScheduledCloseMessageKeys) {
        TransactionUtils.registerSynchronization(new TransactionSynchronization() {
            // 此方法抛出异常不会传播给调用者
            @Override
            public void afterCompletion(int status) {
                switch (status) {
                    case STATUS_COMMITTED -> {
                        // 事务提交暂无操作
                    }
                    case STATUS_ROLLED_BACK -> rollbackOrderScheduledCloseMessageKeys
                            (orderScheduledCloseMessageKeysKey, orderScheduledCloseMessageKeys);
                    case STATUS_UNKNOWN -> {
                        String format = """
                                    Local transaction status for order scheduled close \
                                    message consumption is unknown, orderSn: {}
                                """;
                        log.warn(format, orderSn);
                    }
                    default -> throw new IllegalStateException("Unexpected value: " + status);
                }
            }
        });
    }

    /**
     * 回滚订单定时关闭消息的 KEYS
     * <p>
     * 消费过程幂等性检查时该值被删除，消费重试前要调用此方法以支持重新消费。
     *
     * @param orderScheduledCloseMessageKeysKey 存储订单定时关闭消息键的Redis键。
     * @param orderScheduledCloseMessageKeys    需要回滚的订单编号。
     */
    private void rollbackOrderScheduledCloseMessageKeys(String orderScheduledCloseMessageKeysKey,
                                                        String orderScheduledCloseMessageKeys) {
        try {
            redisTemplate.opsForValue()
                    .set(orderScheduledCloseMessageKeysKey, orderScheduledCloseMessageKeys);
        } catch (Exception e) {
            log.warn("""
                     Failed to rollback orderScheduledCloseMessageKeys: {}, \
                     exception message: {}
                    """, orderScheduledCloseMessageKeys, e.getMessage());
        }
    }

}
