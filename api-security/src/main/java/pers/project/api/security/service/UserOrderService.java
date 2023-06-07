package pers.project.api.security.service;

import com.baomidou.mybatisplus.extension.service.IService;
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
     * 该方法用于生成计数用法订单。计数用法是指接口的调用次数作为使用依据的一种使用方式。
     *
     * @param orderCreationDTO 计数用法订单创建 DTO
     */
    void createQuantityUsageOrder(QuantityUsageOrderCreationDTO orderCreationDTO);

}
