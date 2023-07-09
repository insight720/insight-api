package pers.project.api.facade.message;

import com.alibaba.fastjson.JSON;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.Assert;
import pers.project.api.common.model.dto.QuantityUsageOrderStatusUpdateDTO;
import pers.project.api.common.model.dto.QuantityUsageStockDeductionDTO;
import pers.project.api.common.util.RedisUtils;
import pers.project.api.common.util.TransactionUtils;
import pers.project.api.facade.service.FacadeService;

import static com.baomidou.mybatisplus.core.toolkit.StringPool.COLON;
import static org.apache.rocketmq.spring.support.RocketMQHeaders.KEYS;
import static pers.project.api.common.constant.redis.RedisKeyPrefixConst.QUANTITY_USAGE_ORDER_STATUS_UPDATE_MESSAGE_KEYS_KEY_PREFIX;
import static pers.project.api.common.constant.redis.RedisKeyPrefixConst.QUANTITY_USAGE_STOCK_DEDUCTION_MESSAGE_KEYS_KEY_PREFIX;
import static pers.project.api.common.constant.rocketmq.RocketMQConsumerGroupNameConst.FACADE_QUANTITY_USAGE_STOCK_DEDUCTION_GROUP;
import static pers.project.api.common.constant.rocketmq.RocketMQTagNameConst.QUANTITY_USAGE_ORDER_STATUS_UPDATE_TAG;
import static pers.project.api.common.constant.rocketmq.RocketMQTagNameConst.QUANTITY_USAGE_STOCK_DEDUCTION_TAG;
import static pers.project.api.common.constant.rocketmq.RocketMQTopicNameConst.FACADE_QUANTITY_USAGE_TRANSACTION_TOPIC;
import static pers.project.api.common.constant.rocketmq.RocketMQTopicNameConst.SECURITY_QUANTITY_USAGE_ORDER_NORMAL_TOPIC;

/**
 * 接口计数用法存量扣减监听器
 *
 * @author Luo Fei
 * @date 2023/06/26
 */
@Slf4j
@Component
@RequiredArgsConstructor
@RocketMQMessageListener(topic = FACADE_QUANTITY_USAGE_TRANSACTION_TOPIC,
        selectorExpression = QUANTITY_USAGE_STOCK_DEDUCTION_TAG,
        consumerGroup = FACADE_QUANTITY_USAGE_STOCK_DEDUCTION_GROUP)
public class QuantityUsageStockDeductionListener implements RocketMQListener<MessageExt> {

    /**
     * 接口计数用法订单状态更新消息 destination
     * <p>
     * destination 格式：
     * <pre>
     * "topicName:tags"
     * </pre>
     */
    private static final String QUANTITY_USAGE_ORDER_STATUS_UPDATE_MESSAGE_DESTINATION =
            SECURITY_QUANTITY_USAGE_ORDER_NORMAL_TOPIC
                    + COLON + QUANTITY_USAGE_ORDER_STATUS_UPDATE_TAG;

    private final FacadeService facadeService;

    private final RocketMQTemplate rocketMQTemplate;

    private final TransactionTemplate transactionTemplate;

    private final RedisTemplate<String, Object> redisTemplate;

    private final RedisTemplate<String, Object> redisTransactionTemplate;

    @Override
    public void onMessage(MessageExt stockDeductionMessageExt) {
        // 获取消息体内容
        QuantityUsageStockDeductionDTO stockDeductionDTO = JSON.parseObject
                (stockDeductionMessageExt.getBody(), QuantityUsageStockDeductionDTO.class);
        String orderSn = stockDeductionDTO.getOrderSn();
        // 检查作用于消费过程幂等的令牌
        String stockDeductionMessageKeys = stockDeductionMessageExt.getKeys();
        Assert.notNull(stockDeductionMessageKeys, "The stockDeductionMessageKeys must be not null");
        String stockDeductionMessageKeysKey = QUANTITY_USAGE_STOCK_DEDUCTION_MESSAGE_KEYS_KEY_PREFIX + orderSn;
        boolean isDuplicate = RedisUtils.checkIdempotencyToken
                (redisTemplate, stockDeductionMessageKeysKey, stockDeductionMessageKeys);
        if (isDuplicate) {
            log.info("Duplicate stock deduction message, orderSn: {}", orderSn);
            return;
        }
        // 执行接口计数用法存量更新本地事务，并发送订单状态更新普通消息
        QuantityUsageOrderStatusUpdateDTO orderStatusUpdateDTO = new QuantityUsageOrderStatusUpdateDTO();
        orderStatusUpdateDTO.setOrderSn(stockDeductionDTO.getOrderSn());
        transactionTemplate.executeWithoutResult(ignored -> {
            registerTransactionSynchronization
                    (orderStatusUpdateDTO, stockDeductionMessageKeysKey, stockDeductionMessageKeys);
            facadeService.updateQuantityUsageDeductedStock(stockDeductionDTO, orderStatusUpdateDTO);
            // 设置作用于接口计数用法订单状态更新普通消息消费过程幂等的订单号（Spring 管理的 Redis 事务）
            String orderStatusUpdateMessageKeysKey = QUANTITY_USAGE_ORDER_STATUS_UPDATE_MESSAGE_KEYS_KEY_PREFIX + orderSn;
            redisTransactionTemplate.opsForValue().set(orderStatusUpdateMessageKeysKey, orderSn);
        });
    }

    /**
     * 注册事务同步操作。
     * <p>
     * 这个方法在事务完成后根据事务状态执行同步操作：
     * <pre>
     * 1. 提交：发送接口计数用法订单状态更新普通消息。
     * 2. 回滚：回滚作用于接口计数用法存量扣减消息消费过程幂等的令牌。
     * 3. 未知：打印日志，人工处理。
     * </pre>
     *
     * @param orderStatusUpdateDTO         接口计数用法订单状态更新 DTO
     * @param stockDeductionMessageKeysKey 接口计数用法存量扣减消息 KEYS 键
     */
    private void registerTransactionSynchronization(QuantityUsageOrderStatusUpdateDTO orderStatusUpdateDTO,
                                                    String stockDeductionMessageKeysKey,
                                                    String stockDeductionMessageKeys) {
        TransactionUtils.registerSynchronization(new TransactionSynchronization() {
            // 此方法抛出异常不会传播给调用者
            @Override
            public void afterCompletion(int status) {
                switch (status) {
                    case STATUS_COMMITTED -> {
                        SendResult sendResult;
                        try {
                            Message<QuantityUsageOrderStatusUpdateDTO> orderStatusUpdateMessage = MessageBuilder
                                    .withPayload(orderStatusUpdateDTO)
                                    // 订单号作为消息的 Keys（全局唯一业务索引键）
                                    .setHeader(KEYS, orderStatusUpdateDTO.getOrderSn())
                                    .build();
                            sendResult = rocketMQTemplate.syncSend
                                    (QUANTITY_USAGE_ORDER_STATUS_UPDATE_MESSAGE_DESTINATION, orderStatusUpdateMessage);
                            log.info("""
                                    Order status update message sendStatus: {}, orderSn: {}
                                    """, sendResult.getSendStatus(), orderStatusUpdateDTO.getOrderSn());
                        } catch (Exception e) {
                            log.warn("""
                                    Failed to send order status update message, \
                                    exception message: {}, orderSn: {}
                                    """, e.getMessage(), orderStatusUpdateDTO.getOrderSn());
                        }
                    }
                    case STATUS_ROLLED_BACK -> {
                        try {
                            redisTemplate.opsForValue()
                                    .set(stockDeductionMessageKeysKey, stockDeductionMessageKeys);
                        } catch (Exception e) {
                            log.warn("""
                                     Failed to rollback stockDeductionMessageKeys: {}, \
                                     exception message: {}
                                    """, stockDeductionMessageKeys, e.getMessage());
                        }
                    }
                    // 记录事务状态不确定的日志
                    case STATUS_UNKNOWN -> {
                        String format = """
                                    Local transaction status for stock deduction \
                                    message consumption is unknown, orderSn: {}
                                """;
                        log.warn(format, orderStatusUpdateDTO.getOrderSn());
                    }
                    default -> throw new IllegalStateException("Unexpected value: " + status);
                }
            }
        });
    }

}


