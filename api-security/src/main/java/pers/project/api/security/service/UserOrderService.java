package pers.project.api.security.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.springframework.messaging.Message;
import pers.project.api.security.model.dto.QuantityUsageOrderCreationDTO;
import pers.project.api.security.model.po.UserOrderPO;
import pers.project.api.security.model.query.UserOrderPageQuery;
import pers.project.api.security.model.vo.UserOrderPageVO;

/**
 * 针对表【user_order (用户接口订单) 】的数据库操作 Service
 *
 * @author Luo Fei
 * @date 2023/03/20
 */
public interface UserOrderService extends IService<UserOrderPO> {

    /**
     * 基于提供的 {@code UserOrderPageQuery}，返回一个 {@code UserOrderPageVO} 对象。
     *
     * @param pageQuery 用户订单分页 Query
     * @return 用户订单页面 VO
     */
    UserOrderPageVO getUserOrderPageVO(UserOrderPageQuery pageQuery);

    /**
     * 该方法用于生成接口计数用法订单。
     * <p>
     * 接口计数用法是指以接口的调用次数作为使用依据的一种接口使用方式。
     *
     * @param orderCreationDTO 计数用法订单创建 DTO
     */
    void createQuantityUsageOrder(QuantityUsageOrderCreationDTO orderCreationDTO);

    /**
     * 获取接口计数用法存量扣减消息的本地事务状态。
     *
     * @param message 用户接口计数用法订单状态更新消息
     * @return {@link RocketMQLocalTransactionState} 本地事务状态
     */
    RocketMQLocalTransactionState getQuantityUsageStockDeductionMessageTransactionState(Message<byte[]> message);

}
