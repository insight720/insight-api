package pers.project.api.security.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
import pers.project.api.common.exception.BusinessException;
import pers.project.api.common.model.dto.QuantityUsageStockDeductionDTO;
import pers.project.api.common.util.BeanCopierUtils;
import pers.project.api.common.util.TransactionUtils;
import pers.project.api.security.mapper.UserOrderMapper;
import pers.project.api.security.model.dto.QuantityUsageOrderCreationDTO;
import pers.project.api.security.model.dto.VerificationCodeCheckDTO;
import pers.project.api.security.model.po.UserOrderPO;
import pers.project.api.security.model.query.UserOrderPageQuery;
import pers.project.api.security.model.vo.UserOrderPageVO;
import pers.project.api.security.model.vo.UserOrderVO;
import pers.project.api.security.service.SecurityService;
import pers.project.api.security.service.UserOrderService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.baomidou.mybatisplus.core.toolkit.StringPool.COLON;
import static org.apache.rocketmq.client.producer.LocalTransactionState.COMMIT_MESSAGE;
import static org.apache.rocketmq.spring.core.RocketMQLocalTransactionState.COMMIT;
import static org.apache.rocketmq.spring.core.RocketMQLocalTransactionState.ROLLBACK;
import static org.apache.rocketmq.spring.support.RocketMQHeaders.KEYS;
import static org.springframework.util.StringUtils.hasText;
import static pers.project.api.common.constant.redis.RedisKeyPrefixConst.QUANTITY_USAGE_STOCK_DEDUCTION_MESSAGE_KEYS_KEY_PREFIX;
import static pers.project.api.common.constant.rocketmq.RocketMQTagNameConst.QUANTITY_USAGE_STOCK_DEDUCTION_TAG;
import static pers.project.api.common.constant.rocketmq.RocketMQTopicNameConst.FACADE_QUANTITY_USAGE_TRANSACTION_TOPIC;
import static pers.project.api.common.enumeration.ErrorEnum.SERVER_ERROR;
import static pers.project.api.common.enumeration.QuantityUsageOrderStatusEnum.NEW;
import static pers.project.api.common.enumeration.UsageTypeEnum.QUANTITY_USAGE;
import static pers.project.api.common.util.RocketMQUtils.getTransactionId;

/**
 * 针对表【user_order (用户接口订单) 】的数据库操作 Service 实现
 *
 * @author Luo Fei
 * @date 2023/03/20
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserOrderServiceImpl extends ServiceImpl<UserOrderMapper, UserOrderPO> implements UserOrderService {

    private static final String QUANTITY_USAGE_STOCK_DEDUCTION_MESSAGE_DESTINATION =
            FACADE_QUANTITY_USAGE_TRANSACTION_TOPIC + COLON + QUANTITY_USAGE_STOCK_DEDUCTION_TAG;

    private final SecurityService securityService;

    private final RocketMQTemplate rocketMQTemplate;

    private final RedisTemplate<String, Object> redisTemplate;

    private final RedisTemplate<String, Object> redisTransactionTemplate;

    private final TransactionTemplate transactionTemplate;

    @Override
    public UserOrderPageVO getUserOrderPageVO(UserOrderPageQuery pageQuery) {
        // 按 Query 条件进行分页查询
        LambdaQueryWrapper<UserOrderPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserOrderPO::getAccountId, pageQuery.getAccountId());
        String orderSn = pageQuery.getOrderSn();
        queryWrapper.like(hasText(orderSn), UserOrderPO::getOrderSn, orderSn);
        String description = pageQuery.getDescription();
        queryWrapper.like(hasText(description), UserOrderPO::getDescription, description);
        Set<Integer> usageType = pageQuery.getUsageType();
        queryWrapper.in(CollectionUtils.isNotEmpty(usageType),
                UserOrderPO::getUsageType, usageType);
        Set<Integer> orderStatus = pageQuery.getOrderStatus();
        queryWrapper.in(CollectionUtils.isNotEmpty(usageType),
                UserOrderPO::getOrderStatus, orderStatus);
        LocalDateTime[] createTime = pageQuery.getCreateTime();
        queryWrapper.and(ArrayUtils.isNotEmpty(createTime),
                wrapper -> wrapper.ge(UserOrderPO::getCreateTime, createTime[0])
                        .le(UserOrderPO::getCreateTime, createTime[1]));
        LocalDateTime[] updateTime = pageQuery.getUpdateTime();
        queryWrapper.and(ArrayUtils.isNotEmpty(updateTime),
                wrapper -> wrapper.ge(UserOrderPO::getUpdateTime, updateTime[0])
                        .le(UserOrderPO::getUpdateTime, updateTime[1]));
        Page<UserOrderPO> page = page
                (Page.of(pageQuery.getCurrent(), pageQuery.getSize()), queryWrapper);
        // 转换查询到的分页数据
        List<UserOrderVO> userOrderVOList
                = page.getRecords().stream().map(userOrderPO -> {
            UserOrderVO userOrderVO = new UserOrderVO();
            BeanCopierUtils.copy(userOrderPO, userOrderVO);
            userOrderVO.setOrderId(userOrderPO.getId());
            return userOrderVO;
        }).collect(Collectors.toList());
        UserOrderPageVO pageVO = new UserOrderPageVO();
        pageVO.setTotal(page.getTotal());
        pageVO.setUserOrderVOList(userOrderVOList);
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
                        Local transaction status for quantity usage order insertion is unknown, \
                        orderSn: {} transactionID: {}
                        """;
                log.warn(format, orderSn, getTransactionId(stockDeductionMessage));
            });
            // 订单号作用于消费过程幂等（Spring 管理的 Redis 事务)
            redisTransactionTemplate.opsForValue().set(stockDeductionMessageKeysKey, orderSn);
            // 保存接口计数用法订单记录
            UserOrderPO userOrderPO = new UserOrderPO();
            userOrderPO.setOrderSn(orderSn);
            userOrderPO.setDescription(buildQuantityUsageOrderDescription(orderCreationDTO));
            userOrderPO.setAccountId(orderCreationDTO.getAccountId());
            userOrderPO.setDigestId(orderCreationDTO.getDigestId());
            userOrderPO.setUsageType(QUANTITY_USAGE.storedValue());
            userOrderPO.setOrderStatus(NEW.storedValue());
            save(userOrderPO);
        });
        // 同步发送扣库存消息（事务消息）
        QuantityUsageStockDeductionDTO stockDeductionDTO = new QuantityUsageStockDeductionDTO();
        stockDeductionDTO.setAccountId(orderCreationDTO.getAccountId());
        stockDeductionDTO.setDigestId(orderCreationDTO.getDigestId());
        stockDeductionDTO.setOrderQuantity(orderCreationDTO.getOrderQuantity());
        stockDeductionDTO.setOrderSn(orderSn);
        // 事务消息发送结果（其父类 SendResult 包含更多信息）
        Message<QuantityUsageStockDeductionDTO> stockDeductionMessage = MessageBuilder
                .withPayload(stockDeductionDTO)
                // 订单号作为消息的 Keys（全局唯一业务索引键）
                .setHeader(KEYS, orderSn)
                .build();
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
            (Message<byte[]> stockDeductionMessage) {
        // 在订单表找到了这个订单，说明本地事务插入订单的操作已经正确提交；如果订单表没有订单，说明本地事务已经回滚
        QuantityUsageStockDeductionDTO quantityUsageStockDeductionDTO
                = JSON.parseObject(stockDeductionMessage.getPayload(), QuantityUsageStockDeductionDTO.class);
        LambdaQueryWrapper<UserOrderPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserOrderPO::getOrderSn, quantityUsageStockDeductionDTO.getOrderSn());
        return baseMapper.exists(queryWrapper) ? COMMIT : ROLLBACK;
    }

    /**
     * 构建接口计数用法订单描述信息
     *
     * @param orderCreationDTO 接口计数用法创建 DTO
     * @return 订单描述信息字符串
     */
    private String buildQuantityUsageOrderDescription(QuantityUsageOrderCreationDTO orderCreationDTO) {
        // 根据需求拼接订单描述信息
        return """
                接口名称：%s；接口描述：%s；请求方法：%s；接口地址：%s；接口用法类型：%s；订单锁定的调用次数：%s
                """.formatted(orderCreationDTO.getApiName(),
                orderCreationDTO.getDescription(),
                orderCreationDTO.getMethodSet(),
                orderCreationDTO.getUrl(),
                orderCreationDTO.getUsageTypeSet(),
                orderCreationDTO.getOrderQuantity());
    }

}




