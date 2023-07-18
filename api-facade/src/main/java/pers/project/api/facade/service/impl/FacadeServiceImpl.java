package pers.project.api.facade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import pers.project.api.common.enumeration.UserQuantityUsageStatusEnum;
import pers.project.api.common.exception.DataInconsistencyException;
import pers.project.api.common.model.dto.QuantityUsageApiInfoDTO;
import pers.project.api.common.model.dto.QuantityUsageOrderStatusUpdateDTO;
import pers.project.api.common.model.dto.QuantityUsageStockDeductionDTO;
import pers.project.api.common.model.dto.QuantityUsageStockReleaseDTO;
import pers.project.api.common.model.query.ApiAdminPageQuery;
import pers.project.api.common.model.query.QuantityUsageApiInfoQuery;
import pers.project.api.common.model.query.UserApiDigestPageQuery;
import pers.project.api.common.model.query.UserApiFormatAndQuantityUsageQuery;
import pers.project.api.common.model.vo.*;
import pers.project.api.facade.mapper.*;
import pers.project.api.facade.model.po.ApiDigestPO;
import pers.project.api.facade.model.po.ApiFormatPO;
import pers.project.api.facade.model.po.UserQuantityUsagePO;
import pers.project.api.facade.service.FacadeService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static org.springframework.util.StringUtils.collectionToCommaDelimitedString;
import static org.springframework.util.StringUtils.commaDelimitedListToSet;
import static pers.project.api.common.constant.redis.RedissonNamePrefixConst.*;
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

    private final UserQuantityUsageServiceImpl userQuantityUsageService;

    @Override
    // Suppress warnings for duplicated code lines
    @SuppressWarnings("all")
    public UserApiDigestPageVO getUserApiDigestPageDTO(UserApiDigestPageQuery pageQuery) {
        // 按 Query 条件进行分页查询
        // TODO: 2023/7/16 优化查询
        LambdaQueryWrapper<UserQuantityUsagePO> usageQueryWrapper = new LambdaQueryWrapper<>();
        usageQueryWrapper.select(UserQuantityUsagePO::getDigestId);
        usageQueryWrapper.eq(UserQuantityUsagePO::getAccountId, pageQuery.getAccountId());
        List<UserQuantityUsagePO> userQuantityUsagePOList = userQuantityUsageService.list(usageQueryWrapper);
        if (CollectionUtils.isEmpty(userQuantityUsagePOList)) {
            return new UserApiDigestPageVO();
        }
        List<String> digestIdList = userQuantityUsagePOList.stream()
                .map(UserQuantityUsagePO::getDigestId)
                .collect(Collectors.toList());
        // 按 Query 条件组装 QueryWrapper
        LambdaQueryWrapper<ApiDigestPO> queryWrapper = new LambdaQueryWrapper<>();
        // 用户使用接口的 ID 列表
        // TODO: 2023/7/18 和 ApiDigestServiceImpl.getApiDigestPageVO 唯一不同的地方
        queryWrapper.in(ApiDigestPO::getId, digestIdList);
        // 模糊查询
        String apiName = pageQuery.getApiName();
        queryWrapper.like(nonNull(apiName), ApiDigestPO::getApiName, apiName);
        String description = pageQuery.getDescription();
        queryWrapper.like(nonNull(description), ApiDigestPO::getDescription, description);
        Set<String> methodSet = pageQuery.getMethodSet();
        queryWrapper.like(nonNull(methodSet), ApiDigestPO::getMethod,
                collectionToCommaDelimitedString(methodSet));
        String url = pageQuery.getUrl();
        queryWrapper.like(nonNull(url), ApiDigestPO::getUrl, url);
        // 集合条件
        Set<String> usageTypeSet = pageQuery.getUsageTypeSet();
        queryWrapper.like(nonNull(usageTypeSet), ApiDigestPO::getUsageType,
                collectionToCommaDelimitedString(usageTypeSet));
        Set<Integer> apiStatusSet = pageQuery.getApiStatusSet();
        queryWrapper.in(nonNull(apiStatusSet), ApiDigestPO::getApiStatus, apiStatusSet);
        // 时间范围条件
        LocalDateTime[] createTimeRange = pageQuery.getCreateTimeRange();
        queryWrapper.and(nonNull(createTimeRange),
                wrapper -> wrapper.ge(ApiDigestPO::getCreateTime, createTimeRange[0])
                        .le(ApiDigestPO::getCreateTime, createTimeRange[1]));
        LocalDateTime[] updateTimeRange = pageQuery.getUpdateTimeRange();
        queryWrapper.and(nonNull(updateTimeRange),
                wrapper -> wrapper.ge(ApiDigestPO::getUpdateTime, updateTimeRange[0])
                        .le(ApiDigestPO::getUpdateTime, updateTimeRange[1]));
        // 用 QueryWrapper 分页查询
        Page<ApiDigestPO> page = apiDigestMapper.selectPage
                (Page.of(pageQuery.getCurrent(), pageQuery.getSize()), queryWrapper);
        // 转换查询到的分页数据
        List<UserApiDigestVO> userApiDigestVOList
                = page.getRecords().stream().map(apiDigestPO -> {
            UserApiDigestVO userApiDigestVO = new UserApiDigestVO();
            BeanUtils.copyProperties(apiDigestPO, userApiDigestVO);
            userApiDigestVO.setDigestId(apiDigestPO.getId());
            userApiDigestVO.setUsageTypeSet
                    (commaDelimitedListToSet(apiDigestPO.getUsageType()));
            userApiDigestVO.setMethodSet
                    (commaDelimitedListToSet(apiDigestPO.getMethod()));
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
        BeanUtils.copyProperties(apiFormatPO, formatAndQuantityUsageVO);
        // 查询 API 计数用法信息
        LambdaQueryWrapper<UserQuantityUsagePO> usageQueryWrapper = new LambdaQueryWrapper<>();
        usageQueryWrapper.select(UserQuantityUsagePO::getId, UserQuantityUsagePO::getStock, UserQuantityUsagePO::getUsageStatus);
        usageQueryWrapper.eq(UserQuantityUsagePO::getAccountId, query.getAccountId());
        usageQueryWrapper.eq(UserQuantityUsagePO::getDigestId, query.getDigestId());
        UserQuantityUsagePO userQuantityUsagePo = userQuantityUsageMapper.selectOne(usageQueryWrapper);
        BeanUtils.copyProperties(userQuantityUsagePo, formatAndQuantityUsageVO);
        String usageId = userQuantityUsagePo.getId();
        // 查询接口调用次数统计量和失败调用次数统计量
        String userTotalSemaphoreName = USER_QUANTITY_USAGE_TOTAL_SEMAPHORE_NAME_PREFIX + usageId;
        int total = redissonClient.getSemaphore(userTotalSemaphoreName).availablePermits();
        String userFailureSemaphoreName = USER_QUANTITY_USAGE_FAILURE_SEMAPHORE_NAME_PREFIX + usageId;
        int failure = redissonClient.getSemaphore(userFailureSemaphoreName).availablePermits();
        String userStockSemaphoreName = USER_QUANTITY_USAGE_STOCK_SEMAPHORE_NAME_PREFIX + usageId;
        int stock = redissonClient.getSemaphore(userStockSemaphoreName).availablePermits();
        formatAndQuantityUsageVO.setTotal((long) total);
        formatAndQuantityUsageVO.setFailure((long) failure);
        formatAndQuantityUsageVO.setStock((long) stock);
        // 查询后更新数据库（临时写法）
        // TODO: 2023/7/16 临时写法
        CompletableFuture.runAsync(() -> {
            LambdaUpdateWrapper<UserQuantityUsagePO> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.set(UserQuantityUsagePO::getTotal, total);
            updateWrapper.set(UserQuantityUsagePO::getFailure, failure);
            updateWrapper.set(UserQuantityUsagePO::getStock, stock);
            updateWrapper.eq(UserQuantityUsagePO::getId, usageId);
            userQuantityUsageService.update(updateWrapper);
        }).exceptionally(throwable -> {
            log.warn("Error update user quantity usage, usageId: {}, exception message: {}",
                    usageId, throwable.getMessage());
            return null;
        });
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

    @Override
    public QuantityUsageApiInfoDTO getQuantityUsageApiInfoDTO(QuantityUsageApiInfoQuery apiInfoQuery) {
        // 查询接口摘要数据（可以添加查询状态字段）
        LambdaQueryWrapper<ApiDigestPO> digestQueryWrapper = new LambdaQueryWrapper<>();
        digestQueryWrapper.select(ApiDigestPO::getId);
        digestQueryWrapper.eq(ApiDigestPO::getUrl, apiInfoQuery.getOriginalUrl());
        // method 存储为 , 分隔的请求方法名字符串，必须用模糊匹配查询，可根据需求修改设计
        digestQueryWrapper.like(ApiDigestPO::getMethod, apiInfoQuery.getMethod());
        ApiDigestPO apiDigestPO = apiDigestMapper.selectOne(digestQueryWrapper);
        if (apiDigestPO == null) {
            if (log.isDebugEnabled()) {
                log.debug("Failed to find ApiDigestPO, apiInfoQuery: " + apiInfoQuery);
            }
            return new QuantityUsageApiInfoDTO();
        }
        // 查询用户计数用法数据（可以添加查询状态字段）
        LambdaQueryWrapper<UserQuantityUsagePO> usageQueryWrapper = new LambdaQueryWrapper<>();
        usageQueryWrapper.select(UserQuantityUsagePO::getId);
        usageQueryWrapper.eq(UserQuantityUsagePO::getDigestId, apiDigestPO.getId());
        usageQueryWrapper.eq(UserQuantityUsagePO::getAccountId, apiInfoQuery.getAccountId());
        UserQuantityUsagePO userQuantityUsagePO = userQuantityUsageMapper.selectOne(usageQueryWrapper);
        if (userQuantityUsagePO == null) {
            if (log.isDebugEnabled()) {
                log.debug("Failed to find UserQuantityUsagePO, digestId: {}, accountId: {}",
                        apiDigestPO.getId(), apiInfoQuery.getAccountId());
            }
            return new QuantityUsageApiInfoDTO();
        }
        QuantityUsageApiInfoDTO quantityUsageApiInfoDTO = new QuantityUsageApiInfoDTO();
        quantityUsageApiInfoDTO.setDigestId(apiDigestPO.getId());
        quantityUsageApiInfoDTO.setUsageId(userQuantityUsagePO.getId());
        return quantityUsageApiInfoDTO;
    }

}
