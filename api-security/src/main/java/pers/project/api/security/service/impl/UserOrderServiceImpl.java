package pers.project.api.security.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ArrayUtils;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.project.api.common.model.dto.UserQuantityUsageCreationDTO;
import pers.project.api.common.util.BeanCopierUtils;
import pers.project.api.common.util.TransactionUtils;
import pers.project.api.security.feign.FacadeFeignClient;
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
import java.util.stream.Collectors;

import static org.springframework.util.StringUtils.hasText;
import static pers.project.api.common.enumeration.UsageTypeEnum.QUANTITY_USAGE;

/**
 * 针对表【user_order (用户接口订单) 】的数据库操作 Service 实现
 *
 * @author Luo Fei
 * @date 2023/03/20
 */
@Service
@RequiredArgsConstructor
public class UserOrderServiceImpl extends ServiceImpl<UserOrderMapper, UserOrderPO> implements UserOrderService {

    private final SecurityService securityService;

    private final FacadeFeignClient facadeFeignClient;

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
    // 本地事务
    @Transactional(rollbackFor = Throwable.class)
    public void createQuantityUsageOrder(QuantityUsageOrderCreationDTO orderCreationDTO) {
        // 检查验证码（保证幂等性）
        VerificationCodeCheckDTO codeCheckDTO = orderCreationDTO.getCodeCheckDTO();
        securityService.checkVerificationCode(codeCheckDTO, null);
        // 用户接口计数用法创建 DTO
        UserQuantityUsageCreationDTO usageCreationDTO = new UserQuantityUsageCreationDTO();
        String digestId = orderCreationDTO.getDigestId();
        usageCreationDTO.setDigestId(digestId);
        usageCreationDTO.setAccountId(orderCreationDTO.getAccountId());
        usageCreationDTO.setOrderQuantity(orderCreationDTO.getOrderQuantity());
        // 事务回滚后发送消息让 Facade 模块回滚用户接口计数用法记录的创建
        TransactionUtils.ifRolledBackAfterCompletion(() -> {
                    // 此处抛出异常不会传播给  调用者
                    try {
                    }  catch (Exception e) {
                        log.error("");
                    }
        });
/*
        // 创建一条用户接口计数用法记录（远程调用）
        Result<String> usageCreationResult
                = facadeFeignClient.getUserQuantityUsageCreationResult(usageCreationDTO);
        if (ResultUtils.isFailure(usageCreationResult)) {
            throw new BusinessException(usageCreationResult);
        }
*/
        // 创建新的用户订单
//        String usageId = usageCreationResult.getData();
        UserOrderPO userOrderPO = new UserOrderPO();
        userOrderPO.setOrderSn(IdWorker.getTimeId());
        userOrderPO.setDescription(buildQuantityUsageOrderDescription(orderCreationDTO));
        userOrderPO.setAccountId(orderCreationDTO.getAccountId());
        userOrderPO.setDigestId(digestId);
//        userOrderPO.setUsageId(usageId);
        userOrderPO.setUsageType(QUANTITY_USAGE.storedValue());
        save(userOrderPO);
    }

    /**
     * 构建计数用法订单描述信息
     *
     * @param orderCreationDTO 计数用法订单创建 DTO
     * @return 订单描述信息字符串
     */
    private String buildQuantityUsageOrderDescription(QuantityUsageOrderCreationDTO orderCreationDTO) {
        return """
                接口名称：%s；接口描述：%s；请求方法：%s；接口地址：%s；
                接口用法类型：%s；订单锁定的调用次数：%s
                """.formatted(orderCreationDTO.getApiName(),
                orderCreationDTO.getDescription(),
                orderCreationDTO.getMethodSet(),
                orderCreationDTO.getUrl(),
                orderCreationDTO.getUsageTypeSet(),
                orderCreationDTO.getOrderQuantity());
    }

}




