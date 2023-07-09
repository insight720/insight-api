package pers.project.api.security.model.vo;

import lombok.Data;

import java.util.List;

/**
 * 接口计数用法订单页面 VO
 *
 * @author Luo Fei
 * @date 2023/07/08
 */
@Data
public class QuantityUsageOrderPageVO {

    /**
     * 订单的总数
     */
    private Long total;

    /**
     * 当前页面的接口计数用法订单信息列表
     */
    private List<QuantityUsageOrderVO> quantityUsageOrderVOList;

}
