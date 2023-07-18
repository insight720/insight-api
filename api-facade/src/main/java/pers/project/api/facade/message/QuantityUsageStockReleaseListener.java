package pers.project.api.facade.message;

import com.alibaba.fastjson2.JSON;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.Assert;
import pers.project.api.common.model.dto.QuantityUsageStockReleaseDTO;
import pers.project.api.common.util.RedisUtils;
import pers.project.api.common.util.TransactionUtils;
import pers.project.api.facade.service.FacadeService;

import static pers.project.api.common.constant.redis.RedisKeyPrefixConst.QUANTITY_USAGE_STOCK_RELEASE_MESSAGE_KEYS_KEY_PREFIX;
import static pers.project.api.common.constant.rocketmq.RocketMQConsumerGroupNameConst.FACADE_QUANTITY_USAGE_STOCK_RELEASE_GROUP;
import static pers.project.api.common.constant.rocketmq.RocketMQTagNameConst.QUANTITY_USAGE_STOCK_RELEASE_TAG;
import static pers.project.api.common.constant.rocketmq.RocketMQTopicNameConst.FACADE_QUANTITY_USAGE_TRANSACTION_TOPIC;

/**
 * 接口计数用法存量释放监听器
 *
 * @author Luo Fei
 * @date 2023/07/24
 */
@Slf4j
@Component
@RequiredArgsConstructor
@RocketMQMessageListener(topic = FACADE_QUANTITY_USAGE_TRANSACTION_TOPIC,
        selectorExpression = QUANTITY_USAGE_STOCK_RELEASE_TAG,
        consumerGroup = FACADE_QUANTITY_USAGE_STOCK_RELEASE_GROUP)
public class QuantityUsageStockReleaseListener implements RocketMQListener<MessageExt> {

    private final FacadeService facadeService;

    private final TransactionTemplate transactionTemplate;

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void onMessage(MessageExt stockReleaseMessageExt) {
        // 获取消息体内容
        QuantityUsageStockReleaseDTO stockReleaseDTO = JSON.parseObject
                (stockReleaseMessageExt.getBody(), QuantityUsageStockReleaseDTO.class);
        // 本项目 RocketMQ 消息的 KEYS 值和 订单号是相同的，可以根据需求改为其他值
        String orderSn = stockReleaseDTO.getOrderSn();
        // 检查作用于消费过程幂等的令牌
        String stockReleaseMessageKeys = stockReleaseMessageExt.getKeys();
        Assert.notNull(stockReleaseMessageKeys, "The stockReleaseMessageKeys must be not null");
        String stockReleaseMessageKeysKey = QUANTITY_USAGE_STOCK_RELEASE_MESSAGE_KEYS_KEY_PREFIX + orderSn;
        boolean isIdempotent = RedisUtils.checkIdempotencyToken
                (redisTemplate, stockReleaseMessageKeysKey, stockReleaseMessageKeys);
        if (!isIdempotent) {
            log.info("Duplicate stock release message, orderSn: {}", orderSn);
            return;
        }
        // 执行释放存量的本地事务
        transactionTemplate.executeWithoutResult(ignored -> {
            // 回滚作用于消费过程幂等的令牌
            TransactionUtils.ifRolledBackAfterCompletion(() -> {
                // 此处抛出异常不会传播给调用者
                try {
                    redisTemplate.opsForValue().set(stockReleaseMessageKeysKey, stockReleaseMessageKeys);
                } catch (Exception e) {
                    log.warn("""
                             Failed to rollback stockReleaseMessageKeys: {} \
                             exception message: {}
                            """, stockReleaseMessageKeys, e.getMessage());
                }
            });
            // 打印日志，人工处理
            TransactionUtils.ifUnknownAfterCompletion(() -> {
                String format = """
                            Local transaction status for stock release  \
                            message consumption is unknown, orderSn: {}
                        """;
                log.warn(format, orderSn);
            });
            facadeService.updateQuantityUsageReleasedStock(stockReleaseDTO);
        });
    }

}


