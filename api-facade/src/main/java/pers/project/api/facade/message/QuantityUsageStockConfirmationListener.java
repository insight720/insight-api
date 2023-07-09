package pers.project.api.facade.message;

import com.alibaba.fastjson.JSON;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.Assert;
import pers.project.api.common.exception.DataInconsistencyException;
import pers.project.api.common.model.dto.QuantityUsageStockConfirmationDTO;
import pers.project.api.common.util.RedisUtils;
import pers.project.api.common.util.TransactionUtils;
import pers.project.api.facade.mapper.UserQuantityUsageMapper;

import static pers.project.api.common.constant.redis.RedisKeyPrefixConst.QUANTITY_USAGE_STOCK_CONFIRMATION_MESSAGE_KEYS_KEY_PREFIX;
import static pers.project.api.common.constant.redis.RedissonNamePrefixConst.USER_QUANTITY_USAGE_STOCK_SEMAPHORE_NAME_PREFIX;
import static pers.project.api.common.constant.rocketmq.RocketMQConsumerGroupNameConst.FACADE_QUANTITY_USAGE_STOCK_CONFIRMATION_GROUP;
import static pers.project.api.common.constant.rocketmq.RocketMQTagNameConst.QUANTITY_USAGE_STOCK_CONFIRMATION_TAG;
import static pers.project.api.common.constant.rocketmq.RocketMQTopicNameConst.FACADE_QUANTITY_USAGE_TRANSACTION_TOPIC;

/**
 * 接口计数用法存量确认监听器
 *
 * @author Luo Fei
 * @date 2023/07/06
 */
@Slf4j
@Component
@RequiredArgsConstructor
@RocketMQMessageListener(topic = FACADE_QUANTITY_USAGE_TRANSACTION_TOPIC,
        selectorExpression = QUANTITY_USAGE_STOCK_CONFIRMATION_TAG,
        consumerGroup = FACADE_QUANTITY_USAGE_STOCK_CONFIRMATION_GROUP)
public class QuantityUsageStockConfirmationListener implements RocketMQListener<MessageExt> {

    private final TransactionTemplate transactionTemplate;

    private final RedisTemplate<String, Object> redisTemplate;

    private final UserQuantityUsageMapper userQuantityUsageMapper;

    private final RedissonClient redissonClient;

    @Override
    public void onMessage(MessageExt stockConfirmationMessageExt) {
        // 检查作用于消费过程幂等的令牌
        String stockConfirmationMessageKeys = stockConfirmationMessageExt.getKeys();
        Assert.notNull(stockConfirmationMessageKeys, "The stockConfirmationMessageKeys must be not null");
        // 本项目 RocketMQ 消息的 KEYS 值和 订单号是相同的，可以根据需求改为其他值
        @SuppressWarnings("all")
        String orderSn = stockConfirmationMessageKeys;
        // 检查作用于消费过程幂等的令牌
        String stockConfirmationMessageKeysKey = QUANTITY_USAGE_STOCK_CONFIRMATION_MESSAGE_KEYS_KEY_PREFIX + orderSn;
        boolean isDuplicate = RedisUtils.checkIdempotencyToken
                (redisTemplate, stockConfirmationMessageKeysKey, stockConfirmationMessageKeys);
        if (isDuplicate) {
            log.info("Duplicate stock confirmation message, orderSn: {}", orderSn);
            return;
        }
        // 获取消息体内容
        QuantityUsageStockConfirmationDTO stockConfirmationDTO = JSON.parseObject
                (stockConfirmationMessageExt.getBody(), QuantityUsageStockConfirmationDTO.class);
        // 执行用户接口计数用法存量添加本地事务
        transactionTemplate.executeWithoutResult(ignored -> {
            registerTransactionSynchronization
                    (stockConfirmationDTO, stockConfirmationMessageKeysKey, stockConfirmationMessageKeys);
            int matchedRows = userQuantityUsageMapper.updateConfirmedStockById
                    (stockConfirmationDTO.getUsageId(), stockConfirmationDTO.getQuantity());
            if (matchedRows != 1) {
                // 存量添加失败说明数据产生不一致问题，需要抛出异常，让本地事务回滚，并让消息消费重试
                String message = """
                        Stock confirmation failed due to inconsistent data: %s
                        """.formatted(stockConfirmationDTO);
                throw new DataInconsistencyException(message);
            }
        });
    }

    /**
     * 注册事务同步操作。
     * <p>
     * 这个方法在事务完成后根据事务状态执行同步操作：
     * <pre>
     * 1. 提交：添加用户接口调用次数存量的信号量许可。
     * 2. 回滚：回滚作用于接口计数用法存量确认消息消费过程幂等的令牌。
     * 3. 未知：打印日志，人工处理。
     * </pre>
     *
     * @param stockConfirmationDTO            接口计数用法存量确认 DTO
     * @param stockConfirmationMessageKeys    接口计数用法存量确认消息 KEYS 值
     * @param stockConfirmationMessageKeysKey 接口计数用法存量确认消息 KEYS 键
     */
    private void registerTransactionSynchronization(QuantityUsageStockConfirmationDTO stockConfirmationDTO,
                                                    String stockConfirmationMessageKeysKey,
                                                    String stockConfirmationMessageKeys) {
        TransactionUtils.registerSynchronization(new TransactionSynchronization() {
            // 此方法抛出异常不会传播给调用者
            @Override
            public void afterCompletion(int status) {
                switch (status) {
                    case STATUS_COMMITTED -> {
                        try {
                            String stockSemaphoreName
                                    = USER_QUANTITY_USAGE_STOCK_SEMAPHORE_NAME_PREFIX + stockConfirmationDTO.getUsageId();
                            RSemaphore rSemaphore = redissonClient.getSemaphore(stockSemaphoreName);
                            // RSemaphore 只支持一个 Integer 的数值范围
                            int quantity = Integer.parseInt(stockConfirmationDTO.getQuantity());
                            // 如果信号量原本不存在，此操作会新建
                            rSemaphore.addPermits(quantity);
                        } catch (Exception e) {
                            log.warn("""
                                    Failed to add user quantity usage semaphore permits, \
                                    exception message: {}, usageId: {}
                                    """, e.getMessage(), stockConfirmationDTO.getUsageId());
                        }
                    }
                    case STATUS_ROLLED_BACK -> {
                        try {
                            redisTemplate.opsForValue()
                                    .set(stockConfirmationMessageKeysKey, stockConfirmationMessageKeys);
                        } catch (Exception e) {
                            log.warn("""
                                     Failed to rollback stockConfirmationMessageKeysKey, \
                                     exception message: {}, stockConfirmationMessageKeys: {}
                                    """, e.getMessage(), stockConfirmationMessageKeys);
                        }
                    }
                    case STATUS_UNKNOWN -> {
                        String format = """
                                    Local transaction status for stock confirmation \
                                    message consumption is unknown, stockConfirmationMessageKeys: {}
                                """;
                        log.warn(format, stockConfirmationMessageKeys);
                    }
                    default -> throw new IllegalStateException("Unexpected value: " + status);
                }
            }
        });
    }

}


