package pers.project.api.security.message;

import com.alibaba.fastjson2.JSON;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;
import pers.project.api.common.constant.redis.RedisScriptConst;
import pers.project.api.common.model.dto.QuantityUsageOrderStatusUpdateDTO;

import static pers.project.api.common.constant.rocketmq.RocketMQConsumerGroupNameConst.SECURITY_QUANTITY_USAGE_ORDER_STATUS_UPDATE_GROUP;
import static pers.project.api.common.constant.rocketmq.RocketMQTagNameConst.QUANTITY_USAGE_ORDER_STATUS_UPDATE_TAG;
import static pers.project.api.common.constant.rocketmq.RocketMQTopicNameConst.SECURITY_QUANTITY_USAGE_ORDER_NORMAL_TOPIC;

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

    private final RocketMQTemplate rocketMQTemplate;

    private final TransactionTemplate transactionTemplate;

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 作用于确保幂等性的令牌验证 Redis 脚本
     *
     * @see RedisScriptConst#IDEMPOTENCY_TOKEN_LUA_SCRIPT
     */
    private static final RedisScript<Long> IDEMPOTENCY_TOKEN_REDIS_SCRIPT = RedisScript.of
            (RedisScriptConst.IDEMPOTENCY_TOKEN_LUA_SCRIPT, Long.class);

    @Override
    public void onMessage(MessageExt orderStatusUpdateMessageExt) {
        QuantityUsageOrderStatusUpdateDTO orderStatusUpdateDTO
                = JSON.parseObject(orderStatusUpdateMessageExt.getBody(), QuantityUsageOrderStatusUpdateDTO.class);
        System.out.println("orderStatusUpdateDTO = " + orderStatusUpdateDTO);
    }

}
