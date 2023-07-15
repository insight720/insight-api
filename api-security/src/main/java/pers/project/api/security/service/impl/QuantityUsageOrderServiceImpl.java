package pers.project.api.security.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.ArrayUtils;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import pers.project.api.common.enumeration.UsageTypeEnum;
import pers.project.api.common.exception.BusinessException;
import pers.project.api.common.model.dto.QuantityUsageOrderStatusUpdateDTO;
import pers.project.api.common.model.dto.QuantityUsageStockConfirmationDTO;
import pers.project.api.common.model.dto.QuantityUsageStockDeductionDTO;
import pers.project.api.common.model.dto.QuantityUsageStockReleaseDTO;
import pers.project.api.common.util.BeanCopierUtils;
import pers.project.api.common.util.TransactionUtils;
import pers.project.api.security.mapper.QuantityUsageOrderMapper;
import pers.project.api.security.model.dto.*;
import pers.project.api.security.model.po.QuantityUsageOrderPO;
import pers.project.api.security.model.query.QuantityUsageOrderPageQuery;
import pers.project.api.security.model.vo.QuantityUsageOrderPageVO;
import pers.project.api.security.model.vo.QuantityUsageOrderVO;
import pers.project.api.security.service.QuantityUsageOrderService;
import pers.project.api.security.service.SecurityService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.baomidou.mybatisplus.core.toolkit.StringPool.COLON;
import static java.util.Objects.nonNull;
import static org.apache.rocketmq.client.producer.LocalTransactionState.COMMIT_MESSAGE;
import static org.apache.rocketmq.spring.core.RocketMQLocalTransactionState.COMMIT;
import static org.apache.rocketmq.spring.core.RocketMQLocalTransactionState.ROLLBACK;
import static org.apache.rocketmq.spring.support.RocketMQHeaders.KEYS;
import static pers.project.api.common.constant.redis.RedisKeyPrefixConst.*;
import static pers.project.api.common.constant.rocketmq.RocketMQTagNameConst.*;
import static pers.project.api.common.constant.rocketmq.RocketMQTopicNameConst.FACADE_QUANTITY_USAGE_TRANSACTION_TOPIC;
import static pers.project.api.common.enumeration.ErrorEnum.SERVER_ERROR;
import static pers.project.api.common.enumeration.QuantityUsageOrderStatusEnum.*;
import static pers.project.api.common.util.RocketMQUtils.getTransactionId;

/**
 * 针对表【quantity_usage_order (接口计数用法订单) 】的数据库操作 Service 实现
 *
 * @author Luo Fei
 * @date 2023/03/20
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class QuantityUsageOrderServiceImpl extends ServiceImpl<QuantityUsageOrderMapper, QuantityUsageOrderPO> implements QuantityUsageOrderService {

    /**
     * 接口计数用法存量扣减消息 destination
     * <p>
     * destination 格式：
     * <pre>
     * "topicName:tags"
     * </pre>
     */
    private static final String QUANTITY_USAGE_STOCK_DEDUCTION_MESSAGE_DESTINATION =
            FACADE_QUANTITY_USAGE_TRANSACTION_TOPIC + COLON + QUANTITY_USAGE_STOCK_DEDUCTION_TAG;

    /**
     * 接口计数用法存量释放消息 destination
     * <p>
     * destination 格式：
     * <pre>
     * "topicName:tags"
     * </pre>
     */
    private static final String QUANTITY_USAGE_STOCK_RELEASE_MESSAGE_DESTINATION =
            FACADE_QUANTITY_USAGE_TRANSACTION_TOPIC + COLON + QUANTITY_USAGE_STOCK_RELEASE_TAG;

    /**
     * 接口计数用法存量确认消息 destination
     * <p>
     * destination 格式：
     * <pre>
     * "topicName:tags"
     * </pre>
     */
    private static final String QUANTITY_USAGE_STOCK_CONFIRMATION_MESSAGE_DESTINATION =
            FACADE_QUANTITY_USAGE_TRANSACTION_TOPIC + COLON + QUANTITY_USAGE_STOCK_CONFIRMATION_TAG;

    private final SecurityService securityService;

    private final RocketMQTemplate rocketMQTemplate;

    private final RedisTemplate<String, Object> redisTransactionTemplate;

    private final TransactionTemplate transactionTemplate;

    @Override
    public QuantityUsageOrderPageVO getQuantityUsageOrderPageVO(QuantityUsageOrderPageQuery pageQuery) {
        // 按 Query 条件进行分页查询
        LambdaQueryWrapper<QuantityUsageOrderPO> queryWrapper = new LambdaQueryWrapper<>();
        // 相等条件
        queryWrapper.eq(QuantityUsageOrderPO::getAccountId, pageQuery.getAccountId());
        String quantity = pageQuery.getQuantity();
        queryWrapper.eq(nonNull(quantity), QuantityUsageOrderPO::getQuantity, quantity);
        // 模糊查询
        String orderSn = pageQuery.getOrderSn();
        queryWrapper.like(nonNull(orderSn), QuantityUsageOrderPO::getOrderSn, orderSn);
        String description = pageQuery.getDescription();
        queryWrapper.like(nonNull(description), QuantityUsageOrderPO::getDescription, description);
        // 集合条件
        Set<Integer> orderStatusSet = pageQuery.getOrderStatusSet();
        queryWrapper.in(CollectionUtils.isNotEmpty(orderStatusSet),
                QuantityUsageOrderPO::getOrderStatus, orderStatusSet);
        // 时间范围条件
        LocalDateTime[] createTime = pageQuery.getCreateTimeRange();
        queryWrapper.and(ArrayUtils.isNotEmpty(createTime),
                wrapper -> wrapper.ge(QuantityUsageOrderPO::getCreateTime, createTime[0])
                        .le(QuantityUsageOrderPO::getCreateTime, createTime[1]));
        LocalDateTime[] updateTime = pageQuery.getUpdateTimeRange();
        queryWrapper.and(ArrayUtils.isNotEmpty(updateTime),
                wrapper -> wrapper.ge(QuantityUsageOrderPO::getUpdateTime, updateTime[0])
                        .le(QuantityUsageOrderPO::getUpdateTime, updateTime[1]));
        // 分页查询
        Page<QuantityUsageOrderPO> page = page
                (Page.of(pageQuery.getCurrent(), pageQuery.getSize()), queryWrapper);
        // 转换查询到的分页数据
        List<QuantityUsageOrderVO> quantityUsageOrderVOList
                = page.getRecords().stream().map(quantityUsageOrderPO -> {
            QuantityUsageOrderVO quantityUsageOrderVO = new QuantityUsageOrderVO();
            BeanCopierUtils.copy(quantityUsageOrderPO, quantityUsageOrderVO);
            quantityUsageOrderVO.setOrderId(quantityUsageOrderPO.getId());
            return quantityUsageOrderVO;
        }).collect(Collectors.toList());
        QuantityUsageOrderPageVO pageVO = new QuantityUsageOrderPageVO();
        pageVO.setTotal(page.getTotal());
        pageVO.setQuantityUsageOrderVOList(quantityUsageOrderVOList);
        return pageVO;
    }

    @Override
    public void createQuantityUsageOrder(QuantityUsageOrderCreationDTO orderCreationDTO) {
        // 检查验证码以保证幂等性（LUA 脚本确保检查操作原子性）
        VerificationCodeCheckDTO codeCheckDTO = orderCreationDTO.getCodeCheckDTO();
        securityService.checkVerificationCode(codeCheckDTO, null);
        // 全局唯一的订单号
        String orderSn = IdWorker.getTimeId();
        String stockDeductionMessageKeysKey = QUANTITY_USAGE_STOCK_DEDUCTION_MESSAGE_KEYS_KEY_PREFIX + orderSn;
        // 本地事务
        Consumer<Message<byte[]>> localTransactionExecutionConsumer
                // 回调的消息（用于打印日志等需求）
                = stockDeductionMessage -> transactionTemplate.executeWithoutResult(ignored -> {
            // 记录事务状态不确定的日志
            TransactionUtils.ifUnknownAfterCompletion(() -> {
                String format = """
                        Local transaction status for order creation is unknown, \
                        orderSn: {} transactionID: {}
                        """;
                log.warn(format, orderSn, getTransactionId(stockDeductionMessage));
            });
            // 订单号作用于消费过程幂等（Spring 管理的 Redis 事务)
            redisTransactionTemplate.opsForValue().set(stockDeductionMessageKeysKey, orderSn);
            // 保存接口计数用法订单记录
            QuantityUsageOrderPO quantityUsageOrderPO = new QuantityUsageOrderPO();
            quantityUsageOrderPO.setOrderSn(orderSn);
            quantityUsageOrderPO.setDescription(buildQuantityUsageOrderDescription(orderCreationDTO));
            quantityUsageOrderPO.setAccountId(orderCreationDTO.getAccountId());
            quantityUsageOrderPO.setDigestId(orderCreationDTO.getDigestId());
            quantityUsageOrderPO.setQuantity(orderCreationDTO.getQuantity());
            quantityUsageOrderPO.setOrderStatus(NEW.storedValue());
            save(quantityUsageOrderPO);
        });
        // 发送接口计数用法存量扣减事务消息
        QuantityUsageStockDeductionDTO stockDeductionDTO = new QuantityUsageStockDeductionDTO();
        stockDeductionDTO.setAccountId(orderCreationDTO.getAccountId());
        stockDeductionDTO.setDigestId(orderCreationDTO.getDigestId());
        stockDeductionDTO.setQuantity(orderCreationDTO.getQuantity());
        stockDeductionDTO.setOrderSn(orderSn);
        Message<QuantityUsageStockDeductionDTO> stockDeductionMessage = MessageBuilder
                .withPayload(stockDeductionDTO)
                // 订单号作为消息的 Keys（全局唯一业务索引键）
                .setHeader(KEYS, orderSn)
                .build();
        // 事务消息发送结果（其父类 SendResult 包含更多信息）
        TransactionSendResult transactionSendResult = rocketMQTemplate.sendMessageInTransaction
                (QUANTITY_USAGE_STOCK_DEDUCTION_MESSAGE_DESTINATION, stockDeductionMessage,
                        localTransactionExecutionConsumer);
        log.info("Transaction message sendStatus: {}, orderSn: {}, transaction ID: {}",
                transactionSendResult.getSendStatus(), orderSn, transactionSendResult.getTransactionId());
        if (transactionSendResult.getLocalTransactionState() != COMMIT_MESSAGE) {
            // 抛出异常（本地事务回滚或状态未知)
            throw new BusinessException(SERVER_ERROR, "保存接口计数用法订单记录失败");
        }
    }

    @Override
    public RocketMQLocalTransactionState getQuantityUsageStockDeductionMessageTransactionState
            (Message<byte[]> message) {
        // 在订单表找到了这个订单，说明本地事务插入订单的操作已经正确提交；如果订单表没有订单，说明本地事务已经回滚
        QuantityUsageStockDeductionDTO quantityUsageStockDeductionDTO
                = JSON.parseObject(message.getPayload(), QuantityUsageStockDeductionDTO.class);
        LambdaQueryWrapper<QuantityUsageOrderPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(QuantityUsageOrderPO::getOrderSn, quantityUsageStockDeductionDTO.getOrderSn());
        return baseMapper.exists(queryWrapper) ? COMMIT : ROLLBACK;
    }

    @Override
    public RocketMQLocalTransactionState getQuantityUsageStockReleaseMessageTransactionState
            (Message<byte[]> message) {
        // 判断订单状态是否为超时取消或主动取消
        QuantityUsageStockReleaseDTO stockReleaseDTO
                = JSON.parseObject(message.getPayload(), QuantityUsageStockReleaseDTO.class);
        String orderSn = stockReleaseDTO.getOrderSn();
        LambdaQueryWrapper<QuantityUsageOrderPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(QuantityUsageOrderPO::getOrderStatus);
        queryWrapper.eq(QuantityUsageOrderPO::getOrderSn, orderSn);
        QuantityUsageOrderPO quantityUsageOrderPO = getOne(queryWrapper);
        Integer orderStatus = quantityUsageOrderPO.getOrderStatus();
        boolean isCancelled = TIMEOUT_CANCELLATION.storedValue().equals(orderStatus)
                || USER_CANCELLATION.storedValue().equals(orderStatus);
        return isCancelled ? COMMIT : ROLLBACK;
    }

    @Override
    public void updateQuantityUsageOrderPlacementStatus(QuantityUsageOrderStatusUpdateDTO orderStatusUpdateDTO) {
        LambdaUpdateWrapper<QuantityUsageOrderPO> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(QuantityUsageOrderPO::getOrderSn, orderStatusUpdateDTO.getOrderSn());
        updateWrapper.set(QuantityUsageOrderPO::getOrderStatus, orderStatusUpdateDTO.getOrderStatus());
        updateWrapper.set(QuantityUsageOrderPO::getUsageId, orderStatusUpdateDTO.getUsageId());
        update(updateWrapper);
    }

    @Override
    public void updateQuantityUsageOrderCloseStatus(QuantityUsageOrderScheduledCloseDTO scheduledCloseDTO) {
        // 更新订单状态为超时取消
        LambdaUpdateWrapper<QuantityUsageOrderPO> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(QuantityUsageOrderPO::getOrderStatus, TIMEOUT_CANCELLATION.storedValue());
        updateWrapper.eq(QuantityUsageOrderPO::getOrderSn, scheduledCloseDTO.getOrderSn());
        update(updateWrapper);
    }

    @Override
    public void cancelQuantityUsageOrder(QuantityUsageOrderCancellationDTO cancellationDTO) {
        // 检查验证码以保证幂等性（LUA 脚本确保检查操作原子性）
        VerificationCodeCheckDTO codeCheckDTO = cancellationDTO.getCodeCheckDTO();
        securityService.checkVerificationCode(codeCheckDTO, null);
        // 本地事务
        String orderSn = cancellationDTO.getOrderSn();
        Consumer<Message<byte[]>> localTransactionExecutionConsumer
                // 回调的消息（用于打印日志等需求）
                = stockReleaseMessage -> transactionTemplate.executeWithoutResult(ignored -> {
            // 记录事务状态不确定的日志
            TransactionUtils.ifUnknownAfterCompletion(() -> {
                String format = """
                            Local transaction status for order cancellation \
                            message consumption is unknown, orderSn: {}
                        """;
                log.warn(format, orderSn);
            });
            // 订单号作用于消费过程幂等（Spring 管理的 Redis 事务)
            String stockReleaseMessageKeysKey
                    = QUANTITY_USAGE_STOCK_RELEASE_MESSAGE_KEYS_KEY_PREFIX + orderSn;
            redisTransactionTemplate.opsForValue().set(stockReleaseMessageKeysKey, orderSn);
            // 更新订单状态为主动关闭
            LambdaUpdateWrapper<QuantityUsageOrderPO> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.set(QuantityUsageOrderPO::getOrderStatus, USER_CANCELLATION.storedValue());
            updateWrapper.eq(QuantityUsageOrderPO::getOrderSn, orderSn);
            update(updateWrapper);
        });
        // 发送存量释放事务消息
        TransactionSendResult transactionSendResult = sendStockReleaseTransactionMessage
                (orderSn, localTransactionExecutionConsumer);
        if (transactionSendResult.getLocalTransactionState() != COMMIT_MESSAGE) {
            // 抛出异常（本地事务回滚或状态未知)
            throw new BusinessException(SERVER_ERROR, "取消计数用法订单记录失败");
        }
    }

    @Override
    public void confirmQuantityUsageOrder(QuantityUsageOrderConfirmationDTO orderConfirmationDTO) {
        // 检查验证码以保证幂等性（LUA 脚本确保检查操作原子性）
        VerificationCodeCheckDTO codeCheckDTO = orderConfirmationDTO.getCodeCheckDTO();
        securityService.checkVerificationCode(codeCheckDTO, null);
        // 全局唯一的订单号
        String orderSn = orderConfirmationDTO.getOrderSn();
        // 本地事务
        Consumer<Message<byte[]>> localTransactionExecutionConsumer
                // 回调的消息（用于打印日志等需求）
                = stockConfirmationMessage -> transactionTemplate.executeWithoutResult(ignored -> {
            // 记录事务状态不确定的日志
            TransactionUtils.ifUnknownAfterCompletion(() -> {
                String format = """
                        Local transaction status for order confirmation is unknown, \
                        orderSn: {} transactionID: {}
                        """;
                log.warn(format, orderSn, getTransactionId(stockConfirmationMessage));
            });
            String stockConfirmationMessageKeysKey = QUANTITY_USAGE_STOCK_CONFIRMATION_MESSAGE_KEYS_KEY_PREFIX + orderSn;
            // 订单号作用于消费过程幂等（Spring 管理的 Redis 事务)
            redisTransactionTemplate.opsForValue().set(stockConfirmationMessageKeysKey, orderSn);
            // 更新订单状态为已确认
            LambdaUpdateWrapper<QuantityUsageOrderPO> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.set(QuantityUsageOrderPO::getOrderStatus, CONFIRMATION.storedValue());
            updateWrapper.eq(QuantityUsageOrderPO::getOrderSn, orderSn);
            update(updateWrapper);
        });
        // 发送存量确认事务消息
        QuantityUsageStockConfirmationDTO stockConfirmationDTO = new QuantityUsageStockConfirmationDTO();
        BeanCopierUtils.copy(orderConfirmationDTO, stockConfirmationDTO);
        // 事务消息发送结果（其父类 SendResult 包含更多信息）
        Message<QuantityUsageStockConfirmationDTO> stockConfirmationMessage = MessageBuilder
                .withPayload(stockConfirmationDTO)
                // 订单号作为消息的 Keys（全局唯一业务索引键）
                .setHeader(KEYS, orderSn)
                .build();
        TransactionSendResult transactionSendResult = rocketMQTemplate.sendMessageInTransaction
                (QUANTITY_USAGE_STOCK_CONFIRMATION_MESSAGE_DESTINATION, stockConfirmationMessage,
                        localTransactionExecutionConsumer);
        log.info("Transaction message sendStatus: {}, orderSn: {}, transaction ID: {}",
                transactionSendResult.getSendStatus(), orderSn, transactionSendResult.getTransactionId());
        if (transactionSendResult.getLocalTransactionState() != COMMIT_MESSAGE) {
            // 抛出异常（本地事务回滚或状态未知)
            throw new BusinessException(SERVER_ERROR, "确认接口计数用法订单记录失败");
        }
    }

    /**
     * 构建接口计数用法订单描述信息
     *
     * @param orderCreationDTO 接口计数用法创建 DTO
     * @return 订单描述信息字符串
     */
    private String buildQuantityUsageOrderDescription(QuantityUsageOrderCreationDTO orderCreationDTO) {
        // 根据需求拼接订单描述信息
        List<String> usageTypeDescriptionList = orderCreationDTO.getUsageTypeSet()
                .stream()
                .map(s -> UsageTypeEnum.getByStoredValue(s).description())
                .toList();
        return """
                接口名称：%s；接口描述：%s；所有请求方法：%s；接口地址：%s；所有用法类型：%s；订单锁定的调用次数：%s
                """.formatted(orderCreationDTO.getApiName(),
                orderCreationDTO.getDescription(),
                orderCreationDTO.getMethodSet(),
                orderCreationDTO.getUrl(),
                usageTypeDescriptionList,
                orderCreationDTO.getQuantity());
    }

    /**
     * 发送存量释放事务消息。
     *
     * @param orderSn           订单编号，作为消息的键（全局唯一业务索引键）。
     * @param executionConsumer 执行本地事务的回调函数。
     */
    @Override
    public TransactionSendResult sendStockReleaseTransactionMessage(String orderSn,
                                                                    Consumer<Message<byte[]>> executionConsumer) {
        // 查询接口计数用法存量释放所需要的订单信息
        LambdaQueryWrapper<QuantityUsageOrderPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(QuantityUsageOrderPO::getDigestId, QuantityUsageOrderPO::getQuantity);
        queryWrapper.eq(QuantityUsageOrderPO::getOrderSn, orderSn);
        QuantityUsageOrderPO quantityUsageOrderPO = getOne(queryWrapper);
        QuantityUsageStockReleaseDTO stockReleaseDTO = new QuantityUsageStockReleaseDTO();
        stockReleaseDTO.setOrderSn(orderSn);
        stockReleaseDTO.setDigestId(quantityUsageOrderPO.getDigestId());
        stockReleaseDTO.setQuantity(quantityUsageOrderPO.getQuantity());
        // 事务消息发送结果（其父类 SendResult 包含更多信息）
        Message<QuantityUsageStockReleaseDTO> stockReleaseMessage = MessageBuilder
                .withPayload(stockReleaseDTO)
                // 订单号作为消息的 Keys（全局唯一业务索引键）
                .setHeader(KEYS, orderSn)
                .build();
        TransactionSendResult transactionSendResult = rocketMQTemplate.sendMessageInTransaction
                (QUANTITY_USAGE_STOCK_RELEASE_MESSAGE_DESTINATION, stockReleaseMessage,
                        executionConsumer);
        log.info("Transaction message sendStatus: {}, orderSn: {}, transaction ID: {}",
                transactionSendResult.getSendStatus(), orderSn, transactionSendResult.getTransactionId());
        return transactionSendResult;
    }

    @Override
    public boolean checkWhetherOrderStatusIsSuccess(String orderSn) {
        // 复查订单是否已确认
        LambdaQueryWrapper<QuantityUsageOrderPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(QuantityUsageOrderPO::getOrderStatus);
        queryWrapper.eq(QuantityUsageOrderPO::getOrderSn, orderSn);
        QuantityUsageOrderPO quantityUsageOrderPO = getOne(queryWrapper);
        Integer orderStatus = quantityUsageOrderPO.getOrderStatus();
        return !SUCCESS.storedValue().equals(orderStatus);
    }

}




