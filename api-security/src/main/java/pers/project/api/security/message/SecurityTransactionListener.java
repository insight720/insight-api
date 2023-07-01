package pers.project.api.security.message;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.springframework.messaging.Message;
import org.springframework.util.Assert;
import pers.project.api.common.util.RocketMQUtils;
import pers.project.api.security.service.UserOrderService;

import java.util.function.Consumer;

import static org.apache.rocketmq.spring.core.RocketMQLocalTransactionState.*;
import static org.apache.rocketmq.spring.support.RocketMQHeaders.PREFIX;
import static org.apache.rocketmq.spring.support.RocketMQHeaders.TAGS;
import static pers.project.api.common.constant.rocketmq.RocketMQTagNameConst.QUANTITY_USAGE_STOCK_DEDUCTION_TAG;

/**
 * Security 项目事务监听器
 *
 * @author Luo Fei
 * @date 2023/06/26
 */
@Slf4j
@RequiredArgsConstructor
@RocketMQTransactionListener
public class SecurityTransactionListener implements RocketMQLocalTransactionListener {

    private final UserOrderService userOrderService;

    @Override
    // Suppress warnings for messageConsumer
    @SuppressWarnings("unchecked")
    public RocketMQLocalTransactionState executeLocalTransaction(Message message, Object arg) {
        // Consumer<Message<byte[]>> 是实际执行本地事务的代码（Message 用于回调给事务执行代码）
        Assert.isInstanceOf(Consumer.class, arg, "The arg must be of type Consumer<Message<byte[]>>");
        Consumer<Message<byte[]>> messageConsumer = (Consumer<Message<byte[]>>) arg;
        // transactionId 可用作业务的回查标记
        String transactionId = RocketMQUtils.getTransactionId(message);
        log.info("Local transaction execution, transaction ID: {}", transactionId);
        try {
            // 实际执行本地事务
            messageConsumer.accept(message);
            log.info("Local transaction commit, transaction ID: {}", transactionId);
            // 本地事务正常执行
            return COMMIT;
        } catch (Exception e) {
            log.info("Local transaction rollback, transaction ID: {}, exception message: {}",
                    transactionId, e.getMessage());
            // 本地事务抛出异常
            return ROLLBACK;
        }
    }

    @Override
    public RocketMQLocalTransactionState checkLocalTransaction(Message message) {
        // transactionId 可用作业务的回查标记
        String transactionId = RocketMQUtils.getTransactionId(message);
        // 根据 messageTag 判断本地事务的回查方法
        String messageTag = message.getHeaders().get(PREFIX + TAGS, String.class);
        Assert.notNull(messageTag, "The message tag must be not null");
        try {
            // Suppress warnings for switch and message
            @SuppressWarnings("all")
            RocketMQLocalTransactionState localTransactionState = switch (messageTag) {
                case QUANTITY_USAGE_STOCK_DEDUCTION_TAG ->
                        userOrderService.getQuantityUsageStockDeductionMessageTransactionState(message);
                default -> throw new IllegalStateException("Unexpected value: " + messageTag);
            };
            log.info("""
                    Local transaction check result is {}, transaction ID: {}
                    """, localTransactionState, transactionId);
            return localTransactionState;
        } catch (Exception e) {
            // 本地事务的回查方法抛出异常，无法确定本地事务状态
            log.info("""
                    Local transaction check result is unknown, \
                    transaction ID: {}, exception message: {}
                    """, transactionId, e.getMessage());
            return UNKNOWN;
        }
    }

}
