package pers.project.api.security.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.springframework.messaging.Message;
import pers.project.api.common.enumeration.QuantityUsageOrderStatusEnum;
import pers.project.api.common.model.dto.QuantityUsageOrderStatusUpdateDTO;
import pers.project.api.security.model.dto.QuantityUsageOrderCancellationDTO;
import pers.project.api.security.model.dto.QuantityUsageOrderConfirmationDTO;
import pers.project.api.security.model.dto.QuantityUsageOrderCreationDTO;
import pers.project.api.security.model.dto.QuantityUsageOrderScheduledCloseDTO;
import pers.project.api.security.model.po.QuantityUsageOrderPO;
import pers.project.api.security.model.query.QuantityUsageOrderPageQuery;
import pers.project.api.security.model.vo.QuantityUsageOrderPageVO;

import java.util.function.Consumer;

/**
 * 针对表【quantity_usage_order (接口计数用法订单) 】的数据库操作 Service
 *
 * @author Luo Fei
 * @date 2023/07/03
 */
public interface QuantityUsageOrderService extends IService<QuantityUsageOrderPO> {

    /**
     * 基于提供的 {@code UserOrderPageQuery}，返回一个 {@code UserOrderPageVO} 对象。
     *
     * @param pageQuery 用户订单分页 Query
     * @return 用户订单页面 VO
     */
    QuantityUsageOrderPageVO getQuantityUsageOrderPageVO(QuantityUsageOrderPageQuery pageQuery);

    /**
     * 生成接口计数用法订单。
     * <p>
     * 接口计数用法是指以接口的调用次数作为使用依据的一种接口使用方式。
     *
     * @param orderCreationDTO 计数用法订单创建 DTO
     */
    void createQuantityUsageOrder(QuantityUsageOrderCreationDTO orderCreationDTO);

    /**
     * 获取接口计数用法存量扣减消息的本地事务状态。
     *
     * @param message 用户接口计数用法存量扣减消息
     * @return {@link RocketMQLocalTransactionState} 本地事务状态
     */
    RocketMQLocalTransactionState getQuantityUsageStockDeductionMessageTransactionState(Message<byte[]> message);

    /**
     * 获取接口计数用法存量释放消息的本地事务状态。
     *
     * @param message 用户接口计数用法存量释放消息
     * @return {@link RocketMQLocalTransactionState} 本地事务状态
     */
    RocketMQLocalTransactionState getQuantityUsageStockReleaseMessageTransactionState(Message<byte[]> message);

    /**
     * 更新接口计数用法下单状态。
     * <p>
     * 用于接口计数用法下单结果的状态更新，同时保存 Facade 项目回传的 {@code usageId}。
     *
     * @param orderStatusUpdateDTO 接口计数用法订单状态更新 DTO
     * @see QuantityUsageOrderStatusEnum#storedValue()
     */
    void updateQuantityUsageOrderPlacementStatus(QuantityUsageOrderStatusUpdateDTO orderStatusUpdateDTO);

    /**
     * 更新接口计数用法订单关闭状态。
     * <p>
     * 用于接口计数用法订单定时关闭。
     *
     * @param scheduledCloseDTO 接口计数用法订单定时关闭 DTO
     * @see QuantityUsageOrderStatusEnum#storedValue()
     */
    void updateQuantityUsageOrderCloseStatus(QuantityUsageOrderScheduledCloseDTO scheduledCloseDTO);

    /**
     * 取消接口计数用法订单。
     * <p>
     * 接口计数用法是指以接口的调用次数作为使用依据的一种接口使用方式。
     *
     * @param cancellationDTO 接口计数用法订单取消 DTO
     */
    void cancelQuantityUsageOrder(QuantityUsageOrderCancellationDTO cancellationDTO);

    /**
     * 确认接口计数用法订单。
     * <p>
     * 用于确认接口计数用法订单，标记订单为已使用状态。
     *
     * @param orderConfirmationDTO 接口计数用法订单确认 DTO
     */
    void confirmQuantityUsageOrder(QuantityUsageOrderConfirmationDTO orderConfirmationDTO);

    /**
     * 发送存量释放事务消息。
     * <p>
     * 消息数据在查询后可以修改，需要复查订单更新时间。
     *
     * @param orderSn           订单编号，作为消息的键（全局唯一业务索引键）。
     * @param executionConsumer 执行本地事务的回调函数。
     * @return 事务消息发送结果
     */
    TransactionSendResult sendStockReleaseTransactionMessage(String orderSn,
                                                             Consumer<Message<byte[]>> executionConsumer);

    /**
     * 检查订单状态是否为 "下单成功"。
     * <p>
     * 只有状态为 "下单成功" 的订单才需要定时关闭。
     *
     * @param orderSn 订单编号
     * @return 如果订单已确认，则返回 {@code true}true；否则返回 {@code false}。
     * @see QuantityUsageOrderStatusEnum#storedValue()
     */
    boolean checkWhetherOrderStatusIsSuccess(String orderSn);

}
