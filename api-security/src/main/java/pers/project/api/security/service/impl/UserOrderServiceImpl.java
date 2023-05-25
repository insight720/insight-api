package pers.project.api.security.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ArrayUtils;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import pers.project.api.common.util.BeanCopierUtils;
import pers.project.api.security.mapper.UserOrderMapper;
import pers.project.api.security.model.po.UserOrderPO;
import pers.project.api.security.model.query.UserOrderPageQuery;
import pers.project.api.security.model.vo.UserOrderPageVO;
import pers.project.api.security.model.vo.UserOrderVO;
import pers.project.api.security.service.UserOrderService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.util.StringUtils.hasText;

/**
 * 针对表【user_order (用户接口订单) 】的数据库操作 Service 实现
 *
 * @author Luo Fei
 * @date 2023/03/20
 */
@Service
public class UserOrderServiceImpl extends ServiceImpl<UserOrderMapper, UserOrderPO> implements UserOrderService {

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

}




