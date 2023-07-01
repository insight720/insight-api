package pers.project.api.facade.message;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.Assert;
import pers.project.api.common.constant.redis.RedisScriptConst;
import pers.project.api.common.model.dto.QuantityUsageOrderStatusUpdateDTO;
import pers.project.api.common.model.dto.QuantityUsageStockDeductionDTO;
import pers.project.api.common.util.TransactionUtils;
import pers.project.api.facade.mapper.ApiQuantityUsageMapper;
import pers.project.api.facade.mapper.UserQuantityUsageMapper;
import pers.project.api.facade.model.po.UserQuantityUsagePO;

import java.util.Collections;

import static com.baomidou.mybatisplus.core.toolkit.StringPool.COLON;
import static pers.project.api.common.constant.redis.RedisKeyPrefixConst.QUANTITY_USAGE_STOCK_DEDUCTION_MESSAGE_KEYS_KEY_PREFIX;
import static pers.project.api.common.constant.redis.RedisScriptConst.IDEMPOTENCY_TOKEN_LUA_SCRIPT;
import static pers.project.api.common.constant.rocketmq.RocketMQConsumerGroupNameConst.FACADE_QUANTITY_USAGE_STOCK_DEDUCTION_GROUP;
import static pers.project.api.common.constant.rocketmq.RocketMQTagNameConst.QUANTITY_USAGE_ORDER_STATUS_UPDATE_TAG;
import static pers.project.api.common.constant.rocketmq.RocketMQTagNameConst.QUANTITY_USAGE_STOCK_DEDUCTION_TAG;
import static pers.project.api.common.constant.rocketmq.RocketMQTopicNameConst.FACADE_QUANTITY_USAGE_TRANSACTION_TOPIC;
import static pers.project.api.common.constant.rocketmq.RocketMQTopicNameConst.SECURITY_QUANTITY_USAGE_ORDER_NORMAL_TOPIC;
import static pers.project.api.common.enumeration.QuantityUsageOrderStatusEnum.STOCK_SHORTAGE;
import static pers.project.api.common.enumeration.QuantityUsageOrderStatusEnum.SUCCESS;

/**
 * 接口计数用法库存扣减监听器
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

    private final RocketMQTemplate rocketMQTemplate;

    private final TransactionTemplate transactionTemplate;

    private final RedisTemplate<String, Object> redisTemplate;

    private final ApiQuantityUsageMapper apiQuantityUsageMapper;

    private final UserQuantityUsageMapper userQuantityUsageMapper;

    /**
     * 作用于确保幂等性的令牌验证 Redis 脚本
     *
     * @see RedisScriptConst#IDEMPOTENCY_TOKEN_LUA_SCRIPT
     */
    private static final RedisScript<Long> IDEMPOTENCY_TOKEN_CHECK_REDIS_SCRIPT = RedisScript.of
            (IDEMPOTENCY_TOKEN_LUA_SCRIPT, Long.class);

    @Override
    public void onMessage(MessageExt stockDeductionMessageExt) {
        // 获取消息体内容
        QuantityUsageStockDeductionDTO stockDeductionDTO = JSON.parseObject
                (stockDeductionMessageExt.getBody(), QuantityUsageStockDeductionDTO.class);
        String digestId = stockDeductionDTO.getDigestId();
        String orderQuantity = stockDeductionDTO.getOrderQuantity();
        String accountId = stockDeductionDTO.getAccountId();
        String orderSn = stockDeductionDTO.getOrderSn();
        // 订单号作用于消费过程幂等
        String stockDeductionMessageKeys = stockDeductionMessageExt.getKeys();
        Assert.notNull(stockDeductionMessageKeys, "The stockDeductionMessageKeys must be not null");
        String stockDeductionMessageKeysKey = QUANTITY_USAGE_STOCK_DEDUCTION_MESSAGE_KEYS_KEY_PREFIX + orderSn;
        Long executeResult = redisTemplate.execute
                (IDEMPOTENCY_TOKEN_CHECK_REDIS_SCRIPT,
                        Collections.singletonList(stockDeductionMessageKeysKey), orderSn);
        boolean isDuplicate = (executeResult == null || executeResult == 0L);
        if (isDuplicate) {
            log.info("Duplicate stock deduction message, orderSn: " + orderSn);
            return;
        }
        // 执行扣减接口计数用法存量的本地事务
        transactionTemplate.executeWithoutResult(ignored -> {
            // 订单状态更新的消息体
            QuantityUsageOrderStatusUpdateDTO orderStatusUpdateDTO = new QuantityUsageOrderStatusUpdateDTO();
            orderStatusUpdateDTO.setOrderSn(orderSn);
            // 执行本地事务的同步操作
            TransactionUtils.registerSynchronization(new TransactionSynchronization() {
                // 此方法抛出异常不会传播给调用者
                @Override
                public void afterCompletion(int status) {
                    switch (status) {
                        case STATUS_COMMITTED -> {
                            String destination = SECURITY_QUANTITY_USAGE_ORDER_NORMAL_TOPIC
                                    + COLON + QUANTITY_USAGE_ORDER_STATUS_UPDATE_TAG;
                            SendResult sendResult;
                            try {
                                sendResult = rocketMQTemplate.syncSend(destination, orderStatusUpdateDTO);
                                log.info("""
                                        Quantity usage order status update message sendStatus: {}, orderSn: {}
                                        """, sendResult.getSendStatus(), orderSn);
                            } catch (Exception e) {
                                log.warn("""
                                        Failed to send quantity usage order status update message, \
                                        exception message: {}, orderSn: {}
                                        """, e.getMessage(), orderSn);
                            }
                        }
                        // 回滚作用于消费过程幂等的订单号
                        case STATUS_ROLLED_BACK -> {
                            try {
                                redisTemplate.opsForValue().set(stockDeductionMessageKeysKey, orderSn);
                            } catch (Exception e) {
                                log.warn("""
                                         Failed to rollback stockDeductionMessageKeysKey, \
                                         exception message: {}, stockDeductionMessageKeys: {}
                                        """, e.getMessage(), stockDeductionMessageKeys);
                            }
                        }
                        // 记录事务状态不确定的日志
                        case STATUS_UNKNOWN -> log.warn("""
                                Local transaction status for stock deduction message consumption is unknown, \
                                stockDeductionMessageKeys: {}
                                """, stockDeductionMessageKeys);
                    }
                }
            });
            // 查询原来的用户接口计数用法是否存在
            LambdaQueryWrapper<UserQuantityUsagePO> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.select(UserQuantityUsagePO::getId);
            queryWrapper.eq(UserQuantityUsagePO::getAccountId, accountId);
            queryWrapper.eq(UserQuantityUsagePO::getDigestId, digestId);
            UserQuantityUsagePO originalUserQuantityUsagePO = userQuantityUsageMapper.selectOne(queryWrapper);
            boolean userQuantityUsageExists = (originalUserQuantityUsagePO != null);
            // 先尝试扣减存量，扣减失败则说明存量不足，下单失败
            int matchedRows = apiQuantityUsageMapper.updateStockByDigestId(digestId, orderQuantity);
            if (matchedRows != 1) {
                // 原来的用法主键
                if (userQuantityUsageExists) {
                    orderStatusUpdateDTO.setUsageId(originalUserQuantityUsagePO.getId());
                }
                // 订单状态为存量不足
                orderStatusUpdateDTO.setOrderStatus(STOCK_SHORTAGE.storedValue());
                return;
            }
            // 根据原来的用户接口计数用法是否存在来确定执行更新操作还是插入操作
            if (userQuantityUsageExists) {
                // 更新用户接口计数用法的存量
                userQuantityUsageMapper.updateStockByAccountIdAndDigestId(accountId, digestId, orderQuantity);
                // 原来的用法主键
                orderStatusUpdateDTO.setUsageId(originalUserQuantityUsagePO.getId());
                // 订单状态为下单成功
                orderStatusUpdateDTO.setOrderStatus(SUCCESS.storedValue());
                return;
            }
            // 保存新的用户接口计数用法
            UserQuantityUsagePO newUserQuantityUsagePO = new UserQuantityUsagePO();
            newUserQuantityUsagePO.setAccountId(accountId);
            newUserQuantityUsagePO.setDigestId(digestId);
            newUserQuantityUsagePO.setTotal(orderQuantity);
            userQuantityUsageMapper.insert(newUserQuantityUsagePO);
            // 新的用法主键
            orderStatusUpdateDTO.setUsageId(newUserQuantityUsagePO.getId());
            // 订单状态为下单成功
            orderStatusUpdateDTO.setOrderStatus(SUCCESS.storedValue());
        });
    }

}
