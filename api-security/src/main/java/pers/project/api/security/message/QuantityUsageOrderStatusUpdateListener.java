package pers.project.api.security.message;

import com.alibaba.fastjson2.JSON;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import pers.project.api.common.model.dto.QuantityUsageOrderStatusUpdateDTO;
import pers.project.api.common.util.RedisUtils;
import pers.project.api.security.service.UserOrderService;

import static pers.project.api.common.constant.redis.RedisKeyPrefixConst.QUANTITY_USAGE_ORDER_STATUS_UPDATE_MESSAGE_KEYS_KEY_PREFIX;
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

    private final UserOrderService userOrderService;

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void onMessage(MessageExt orderStatusUpdateMessageExt) {
        QuantityUsageOrderStatusUpdateDTO orderStatusUpdateDTO
                = JSON.parseObject(orderStatusUpdateMessageExt.getBody(), QuantityUsageOrderStatusUpdateDTO.class);
        // 检查作用于消费过程幂等的令牌
        String orderSn = orderStatusUpdateDTO.getOrderSn();
        String orderStatusUpdateKeysKey = QUANTITY_USAGE_ORDER_STATUS_UPDATE_MESSAGE_KEYS_KEY_PREFIX + orderSn;
        boolean isDuplicate = RedisUtils.checkIdempotencyToken(redisTemplate, orderStatusUpdateKeysKey, orderSn);
        if (isDuplicate) {
            log.info("Duplicate order status update message, orderSn: {}", orderSn);
            return;
        }
    }

}
