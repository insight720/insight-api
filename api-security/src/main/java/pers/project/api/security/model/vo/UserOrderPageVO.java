package pers.project.api.security.model.vo;

import lombok.Data;

import java.util.List;

/**
 * 用户订单页面 VO
 *
 * @author Luo Fei
 * @date 2023/05/16
 */
@Data
public class UserOrderPageVO {

    /**
     * 用户订单的总数
     */
    private Long total;

    /**
     * 当前页面的用户订单信息列表
     */
    private List<UserOrderVO> userOrderVOList;

}
