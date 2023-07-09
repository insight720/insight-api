package pers.project.api.facade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ArrayUtils;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import pers.project.api.common.enumeration.UserQuantityUsageStatusEnum;
import pers.project.api.common.exception.DataInconsistencyException;
import pers.project.api.common.model.dto.QuantityUsageOrderStatusUpdateDTO;
import pers.project.api.common.model.dto.QuantityUsageStockDeductionDTO;
import pers.project.api.common.model.dto.QuantityUsageStockReleaseDTO;
import pers.project.api.common.model.query.ApiAdminPageQuery;
import pers.project.api.common.model.query.UserApiDigestPageQuery;
import pers.project.api.common.model.query.UserApiFormatAndQuantityUsageQuery;
import pers.project.api.common.model.vo.*;
import pers.project.api.common.util.BeanCopierUtils;
import pers.project.api.facade.mapper.*;
import pers.project.api.facade.model.po.ApiDigestPO;
import pers.project.api.facade.model.po.ApiFormatPO;
import pers.project.api.facade.model.po.UserQuantityUsagePO;
import pers.project.api.facade.service.FacadeService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.util.StringUtils.hasText;
import static pers.project.api.common.enumeration.QuantityUsageOrderStatusEnum.STOCK_SHORTAGE;
import static pers.project.api.common.enumeration.QuantityUsageOrderStatusEnum.SUCCESS;

/**
 * Facade 项目的 Service 实现
 *
 * @author Luo Fei
 * @date 2023/05/05
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FacadeServiceImpl implements FacadeService {

    private final FacadeMapper facadeMapper;

    private final ApiDigestMapper apiDigestMapper;

    private final ApiFormatMapper apiFormatMapper;

    private final ApiQuantityUsageMapper apiQuantityUsageMapper;

    private final UserQuantityUsageMapper userQuantityUsageMapper;

    private final RedissonClient redissonClient;

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public UserApiDigestPageVO getUserApiDigestPageDTO(UserApiDigestPageQuery pageQuery) {
        // 按 Query 条件进行分页查询
        LambdaQueryWrapper<ApiDigestPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(ApiDigestPO::getId, ApiDigestPO::getApiName, ApiDigestPO::getDescription,
                ApiDigestPO::getMethod, ApiDigestPO::getUrl, ApiDigestPO::getUsageType,
                ApiDigestPO::getApiStatus, ApiDigestPO::getCreateTime, ApiDigestPO::getUpdateTime);
        queryWrapper.eq(ApiDigestPO::getAccountId, pageQuery.getAccountId());
        queryWrapper.like(hasText(pageQuery.getApiName()),
                ApiDigestPO::getApiName, pageQuery.getApiName());
        queryWrapper.like(hasText(pageQuery.getDescription()),
                ApiDigestPO::getDescription, pageQuery.getDescription());
        queryWrapper.in((CollectionUtils.isNotEmpty(pageQuery.getMethod())),
                ApiDigestPO::getApiStatus, pageQuery.getMethod());
        queryWrapper.like(hasText(pageQuery.getUrl()),
                ApiDigestPO::getUrl, pageQuery.getUrl());
        queryWrapper.in(CollectionUtils.isNotEmpty(pageQuery.getUsageType()),
                ApiDigestPO::getApiStatus, pageQuery.getUsageType());
        queryWrapper.in(CollectionUtils.isNotEmpty(pageQuery.getApiStatus()),
                ApiDigestPO::getApiStatus, pageQuery.getApiStatus());
        LocalDateTime[] createTime = pageQuery.getCreateTime();
        queryWrapper.and(ArrayUtils.isNotEmpty(createTime),
                wrapper -> wrapper.ge(ApiDigestPO::getCreateTime, createTime[0])
                        .le(ApiDigestPO::getCreateTime, createTime[1]));
        LocalDateTime[] updateTime = pageQuery.getUpdateTime();
        queryWrapper.and(ArrayUtils.isNotEmpty(updateTime),
                wrapper -> wrapper.ge(ApiDigestPO::getUpdateTime, updateTime[0])
                        .le(ApiDigestPO::getUpdateTime, updateTime[1]));
        Page<ApiDigestPO> page = apiDigestMapper.selectPage
                (Page.of(pageQuery.getCurrent(), pageQuery.getSize()), queryWrapper);
        // 转换查询到的分页数据
        List<UserApiDigestVO> userApiDigestVOList
                = page.getRecords().stream().map(apiDigestPO -> {
            UserApiDigestVO userApiDigestVO = new UserApiDigestVO();
            BeanCopierUtils.copy(apiDigestPO, userApiDigestVO);
            userApiDigestVO.setDigestId(apiDigestPO.getId());
            return userApiDigestVO;
        }).collect(Collectors.toList());
        UserApiDigestPageVO pageDTO = new UserApiDigestPageVO();
        pageDTO.setTotal(page.getTotal());
        pageDTO.setDigestVOList(userApiDigestVOList);
        return pageDTO;
    }

    @Override
    public UserApiFormatAndQuantityUsageVO getUserApiFormatAndQuantityUsageVO(UserApiFormatAndQuantityUsageQuery query) {
        UserApiFormatAndQuantityUsageVO formatAndQuantityUsageVO = new UserApiFormatAndQuantityUsageVO();
        // 查询 API 格式信息
        LambdaQueryWrapper<ApiFormatPO> formatQueryWrapper = new LambdaQueryWrapper<>();
        formatQueryWrapper.select(ApiFormatPO::getRequestParam, ApiFormatPO::getRequestHeader,
                ApiFormatPO::getRequestBody, ApiFormatPO::getResponseHeader, ApiFormatPO::getResponseBody);
        formatQueryWrapper.eq(ApiFormatPO::getDigestId, query.getDigestId());
        ApiFormatPO apiFormatPO = apiFormatMapper.selectOne(formatQueryWrapper);
        BeanCopierUtils.copy(apiFormatPO, formatAndQuantityUsageVO);
        // 查询 API 计数用法信息
        LambdaQueryWrapper<UserQuantityUsagePO> usageQueryWrapper = new LambdaQueryWrapper<>();
        usageQueryWrapper.select(UserQuantityUsagePO::getTotal, UserQuantityUsagePO::getFailure,
                UserQuantityUsagePO::getStock, UserQuantityUsagePO::getUsageStatus);
        usageQueryWrapper.eq(UserQuantityUsagePO::getAccountId, query.getAccountId());
        usageQueryWrapper.eq(UserQuantityUsagePO::getDigestId, query.getDigestId());
        UserQuantityUsagePO userQuantityUsagePo = userQuantityUsageMapper.selectOne(usageQueryWrapper);
        BeanCopierUtils.copy(userQuantityUsagePo, formatAndQuantityUsageVO);
        return formatAndQuantityUsageVO;
    }

    @Override
    public ApiAdminPageVO getApiAdminPageVO(ApiAdminPageQuery pageQuery) {
        Long total = facadeMapper.countApiAdminVOs(pageQuery);
        if (total == 0L) {
            return new ApiAdminPageVO();
        }
        List<ApiAdminVO> apiAdminVOList = facadeMapper.listApiAdminVOs(pageQuery);
        ApiAdminPageVO apiAdminPageVO = new ApiAdminPageVO();
        apiAdminPageVO.setTotal(total);
        apiAdminPageVO.setApiAdminVOList(apiAdminVOList);
        return apiAdminPageVO;
    }

    @Override
    public void updateQuantityUsageDeductedStock(QuantityUsageStockDeductionDTO stockDeductionDTO, QuantityUsageOrderStatusUpdateDTO orderStatusUpdateDTO) {
        // 先尝试扣减存量，扣减失败则说明存量不足，下单失败
        String digestId = stockDeductionDTO.getDigestId();
        String quantity = stockDeductionDTO.getQuantity();
        int matchedRows = apiQuantityUsageMapper.updateDeductedStockByDigestId(digestId, quantity);
        if (matchedRows != 1) {
            // 订单状态为存量不足
            orderStatusUpdateDTO.setOrderStatus(STOCK_SHORTAGE.storedValue());
            return;
        }
        // 订单状态为下单成功
        orderStatusUpdateDTO.setOrderStatus(SUCCESS.storedValue());
        // 查询原来的用户接口计数用法是否存在
        LambdaQueryWrapper<UserQuantityUsagePO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(UserQuantityUsagePO::getId);
        queryWrapper.eq(UserQuantityUsagePO::getDigestId, digestId);
        String accountId = stockDeductionDTO.getAccountId();
        queryWrapper.eq(UserQuantityUsagePO::getAccountId, accountId);
        UserQuantityUsagePO originalUserQuantityUsagePO = userQuantityUsageMapper.selectOne(queryWrapper);
        boolean userQuantityUsageExists = (originalUserQuantityUsagePO != null);
        // 根据原来的用户接口计数用法是否存在来确定执行更新操作还是插入操作
        if (userQuantityUsageExists) {
            // 原来的用法主键
            orderStatusUpdateDTO.setUsageId(originalUserQuantityUsagePO.getId());
            return;
        }
        // 保存新的用户接口计数用法
        UserQuantityUsagePO newUserQuantityUsagePO = new UserQuantityUsagePO();
        newUserQuantityUsagePO.setAccountId(accountId);
        newUserQuantityUsagePO.setDigestId(digestId);
        newUserQuantityUsagePO.setUsageStatus(UserQuantityUsageStatusEnum.NEW.storedValue());
        userQuantityUsageMapper.insert(newUserQuantityUsagePO);
        // 新的用法主键
        orderStatusUpdateDTO.setUsageId(newUserQuantityUsagePO.getId());
    }

    @Override
    public void updateQuantityUsageReleasedStock(QuantityUsageStockReleaseDTO stockReleaseDTO) {
        // 释放接口计数用法存量
        String digestId = stockReleaseDTO.getDigestId();
        String quantity = stockReleaseDTO.getQuantity();
        int matchedRows = apiQuantityUsageMapper.updateReleasedStockByDigestId(digestId, quantity);
        if (matchedRows != 1) {
            // 存量添加失败说明数据产生不一致问题，需要抛出异常，让本地事务回滚，并让消息消费重试
            String message = """
                    Stock release failed due to inconsistent data: %s
                    """.formatted(stockReleaseDTO);
            throw new DataInconsistencyException(message);
        }
    }

}
