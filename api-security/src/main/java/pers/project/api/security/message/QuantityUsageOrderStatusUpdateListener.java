package pers.project.api.security.message;

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
import pers.project.api.common.enumeration.QuantityUsageOrderStatusEnum;
import pers.project.api.common.util.TransactionUtils;
import pers.project.api.security.model.dto.QuantityUsageOrderScheduledCloseDTO;
import pers.project.api.security.service.QuantityUsageOrderService;

import static com.baomidou.mybatisplus.core.toolkit.StringPool.COLON;
import static org.apache.rocketmq.spring.support.RocketMQHeaders.KEYS;
import static pers.project.api.common.constant.rocketmq.RocketMQConsumerGroupNameConst.SECURITY_QUANTITY_USAGE_ORDER_STATUS_UPDATE_GROUP;
import static pers.project.api.common.constant.rocketmq.RocketMQTagNameConst.QUANTITY_USAGE_ORDER_SCHEDULED_CLOSE_TAG;
import static pers.project.api.common.constant.rocketmq.RocketMQTagNameConst.QUANTITY_USAGE_ORDER_STATUS_UPDATE_TAG;
import static pers.project.api.common.constant.rocketmq.RocketMQTopicNameConst.SECURITY_QUANTITY_USAGE_ORDER_DELAY_TOPIC;
import static pers.project.api.common.constant.rocketmq.RocketMQTopicNameConst.SECURITY_QUANTITY_USAGE_ORDER_NORMAL_TOPIC;
import static pers.project.api.common.enumeration.QuantityUsageOrderStatusEnum.SUCCESS;

/**
 * 接口计数用法订单状态更新监听器
 *
 * @author Luo Fei
 * @date 2023/06/29
 */
@Slf4j
@Component
@RequiredArgsConstructor
@RocketMQMessageListener
        (topic = SECURITY_QUANTITY_USAGE_ORDER_NORMAL_TOPIC,
                selectorExpression = QUANTITY_USAGE_ORDER_STATUS_UPDATE_TAG,
                consumerGroup = SECURITY_QUANTITY_USAGE_ORDER_STATUS_UPDATE_GROUP)
public class QuantityUsageOrderStatusUpdateListener implements RocketMQListener<MessageExt> {

    /**
     * 接口计数用法订单定时关闭延时消息 destination
     * <p>
     * destination 格式：
     * <pre>
     * "topicName:tags"
     * </pre>
     */
    private static final String QUANTITY_USAGE_ORDER_SCHEDULED_CLOSE_MESSAGE_DESTINATION =
            SECURITY_QUANTITY_USAGE_ORDER_DELAY_TOPIC
                    + COLON + QUANTITY_USAGE_ORDER_SCHEDULED_CLOSE_TAG;

    /**
     * 接口计数用法订单定时关闭延时时间（单位：秒）
     */
    private static final long QUANTITY_USAGE_ORDER_SCHEDULED_CLOSE_DELAY_TIME_SECONDS = 60L;

    private final QuantityUsageOrderService userOrderService;

    private final RocketMQTemplate rocketMQTemplate;

    private final TransactionTemplate transactionTemplate;

    private final RedisTemplate<String, Object> redisTemplate;

    private final RedisTemplate<String, Object> redisTransactionTemplate;

    @Override
    public void onMessage(MessageExt orderStatusUpdateMessageExt) {
        // 获取消息体内容
//        QuantityUsageOrderStatusUpdateDTO orderStatusUpdateDTO
//                = JSON.parseObject(orderStatusUpdateMessageExt.getBody(), QuantityUsageOrderStatusUpdateDTO.class);
//        String orderSn = orderStatusUpdateDTO.getOrderSn();
//        // 检查作用于消费过程幂等的令牌
//        String orderStatusUpdateMessageKeys = orderStatusUpdateMessageExt.getKeys();
//        Assert.notNull(orderStatusUpdateMessageKeys, "The orderStatusUpdateMessageKeys must be not null");
//        String orderStatusUpdateKeysKey = QUANTITY_USAGE_ORDER_STATUS_UPDATE_MESSAGE_KEYS_KEY_PREFIX + orderSn;
//        boolean isDuplicate = RedisUtils.checkIdempotencyToken
//                (redisTemplate, orderStatusUpdateKeysKey, orderStatusUpdateMessageKeys);
//        if (isDuplicate) {
//            log.info("Duplicate order status update message, orderSn: {}", orderSn);
//            return;
//        }
//        // 执行接口计数用法订单状态更新本地事务，并发送接口计数用法订单定时关闭延时消息
//        QuantityUsageOrderScheduledCloseDTO orderScheduledCloseDTO = new QuantityUsageOrderScheduledCloseDTO();
//        orderScheduledCloseDTO.setOrderSn(orderSn);
//        transactionTemplate.executeWithoutResult(ignored -> {
//            registerTransactionSynchronization(orderScheduledCloseDTO, orderStatusUpdateDTO.getOrderStatus(),
//                    orderStatusUpdateKeysKey, orderStatusUpdateMessageKeys);
//            // 改变订单状态
//            userOrderService.updateQuantityUsageOrderPlacementStatus(orderStatusUpdateDTO);
//            // 设置作用于接口计数用法订单定时关闭延时消息消费过程幂等的订单号（Spring 管理的 Redis 事务）
//            String orderScheduledCloseMessageKeysKey
//                    = QUANTITY_USAGE_ORDER_SCHEDULED_CLOSE_MESSAGE_KEYS_KEY_PREFIX + orderSn;
//            redisTransactionTemplate.opsForValue().set(orderScheduledCloseMessageKeysKey, orderSn);
//        });
    }

    /**
     * 注册事务同步操作。
     * <p>
     * 这个方法在事务完成后根据事务状态执行同步操作：
     * <pre>
     * 1. 提交：如果下单成功，发送接口计数用法订单定时关闭延时消息。
     * 2. 回滚：回滚作用于接口计数用法订单状态更新消息消费过程幂等的订单号。
     * 3. 未知：打印日志，人工处理。
     * </pre>
     *
     * @param orderScheduledCloseDTO   接口计数用法订单定时关闭 DTO
     * @param orderStatus              订单状态值
     * @param orderStatusUpdateKeysKey 接口计数用法订单状态更新消息 KEYS 键
     * @param orderStatusUpdateKeys    接口计数用法订单状态更新消息 KEYS 值
     * @see QuantityUsageOrderStatusEnum#storedValue()
     */
    private void registerTransactionSynchronization(QuantityUsageOrderScheduledCloseDTO orderScheduledCloseDTO,
                                                    Integer orderStatus,
                                                    String orderStatusUpdateKeysKey,
                                                    String orderStatusUpdateKeys) {
        TransactionUtils.registerSynchronization(new TransactionSynchronization() {
            // 此方法抛出异常不会传播给调用者
            @Override
            public void afterCompletion(int status) {
                switch (status) {
                    case STATUS_COMMITTED -> {
                        // 下单成功才需要发送定时关单消息
                        if (!SUCCESS.storedValue().equals(orderStatus)) {
                            return;
                        }
                        String orderSn = orderScheduledCloseDTO.getOrderSn();
                        Message<QuantityUsageOrderScheduledCloseDTO> scheduledCloseDTOMessage = MessageBuilder
                                .withPayload(orderScheduledCloseDTO)
                                // 订单号作为消息的 Keys（全局唯一业务索引键）
                                .setHeader(KEYS, orderSn)
                                .build();
                        SendResult sendResult;
                        try {
                            sendResult = rocketMQTemplate.syncSendDelayTimeSeconds
                                    (QUANTITY_USAGE_ORDER_SCHEDULED_CLOSE_MESSAGE_DESTINATION,
                                            scheduledCloseDTOMessage,
                                            QUANTITY_USAGE_ORDER_SCHEDULED_CLOSE_DELAY_TIME_SECONDS);
                        } catch (Exception e) {
                            log.warn("""
                                    Failed to send order scheduled close message, \
                                    exception message: {}, orderSn: {}
                                    """, e.getMessage(), orderSn);
                            return;
                        }
                        log.info("""
                                Order scheduled close message sendStatus: {}, orderSn: {}
                                """, sendResult.getSendStatus(), orderSn);
                    }
                    case STATUS_ROLLED_BACK -> {
                        try {
                            redisTemplate.opsForValue()
                                    .set(orderStatusUpdateKeysKey, orderStatusUpdateKeys);
                        } catch (Exception e) {
                            log.warn("""
                                     Failed to rollback stockDeductionMessageKeys: {}, \
                                     exception message: {}
                                    """, orderStatusUpdateKeys, e.getMessage());
                        }
                    }
                    case STATUS_UNKNOWN -> {
                        String format = """
                                    Local transaction status for order status update \
                                    message consumption is unknown, orderSn: {}
                                """;
                        log.warn(format, orderScheduledCloseDTO.getOrderSn());
                    }
                    default -> throw new IllegalStateException("Unexpected value: " + status);
                }
            }
        });
    }

}
