package pers.project.api.facade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ArrayUtils;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.project.api.common.enumeration.ErrorEnum;
import pers.project.api.common.exception.BusinessException;
import pers.project.api.common.model.dto.UserQuantityUsageCreationDTO;
import pers.project.api.common.model.query.ApiAdminPageQuery;
import pers.project.api.common.model.query.UserApiDigestPageQuery;
import pers.project.api.common.model.query.UserApiFormatAndQuantityUsageQuery;
import pers.project.api.common.model.vo.*;
import pers.project.api.common.util.BeanCopierUtils;
import pers.project.api.common.util.TransactionUtils;
import pers.project.api.facade.mapper.*;
import pers.project.api.facade.model.po.ApiDigestPO;
import pers.project.api.facade.model.po.ApiFormatPO;
import pers.project.api.facade.model.po.UserQuantityUsagePO;
import pers.project.api.facade.service.FacadeService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.util.StringUtils.hasText;
import static pers.project.api.common.constant.RedissonNamePrefixConst.USER_QUANTITY_USAGE_SEMAPHORE_KEY_PREFIX;

/**
 * Facade 模块的 Service 实现
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
    // 本地事务
    @Transactional(rollbackFor = Throwable.class)
    public String createUserQuantityUsage(UserQuantityUsageCreationDTO creationDTO) {
        // TODO: 2023/6/23 接口幂等性
        // 首先更新接口计数用法数据，若库存量不够则下单失败
        String digestId = creationDTO.getDigestId();
        String orderQuantity = creationDTO.getOrderQuantity();
        int matchedRows = apiQuantityUsageMapper
                .updateStockByDigestId(digestId, orderQuantity);
        if (matchedRows != 1) {
            throw new BusinessException(ErrorEnum.QUANTITY_EXCEEDED_ERROR, "订单数量超出库存量");
        }
//        TransactionUtils.beforeReadWriteCommit();
        // 查询是否存在原来的接口计数用法
        LambdaQueryWrapper<UserQuantityUsagePO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(UserQuantityUsagePO::getId);
        String accountId = creationDTO.getAccountId();
        queryWrapper.eq(UserQuantityUsagePO::getAccountId, accountId);
        queryWrapper.eq(UserQuantityUsagePO::getDigestId, digestId);
        UserQuantityUsagePO originalUserQuantityUsagePO
                = userQuantityUsageMapper.selectOne(queryWrapper);
        boolean userQuantityUsageExists = (originalUserQuantityUsagePO != null);
        // TODO: 2023/6/23 信号量内容移动到订单确认那里
        // 获取接口计数用法信号量
        String userQuantityUsageSemaphoreName = USER_QUANTITY_USAGE_SEMAPHORE_KEY_PREFIX
                + creationDTO.getAccountId() + creationDTO.getDigestId();
        RSemaphore userQuantityUsageSemaphore
                = redissonClient.getSemaphore(userQuantityUsageSemaphoreName);
        // TODO: 2023/6/19 事务成功再操作，而不是回滚
        // 本地事务的回滚操作
        TransactionUtils.ifRolledBackAfterCompletion(() -> {
            // 此处抛出异常不会传播给调用者
            try {
                // 根据原来的用户接口计数用法是否存在来确定回滚信号量更新操作还是新建操作
                if (userQuantityUsageExists) {
                    // 回滚信号量许可数量
                    userQuantityUsageSemaphore.addPermits
                            // 只支持 int 类型
                                    (-Integer.parseInt(orderQuantity));
                } else {
                    // 回滚新建的信号量
                    userQuantityUsageSemaphore.delete();
                }
            } catch (Exception e) {
                    log.error("""
                            Error rolling back semaphore {} after user quantity usage creation. \
                            The following creationDTO was not rolled back: {}
                            """, userQuantityUsageExists ? "update" : "creation", creationDTO);
            }
        });
        // 根据原来的用户接口计数用法是否存在来确定执行更新操作还是插入操作
        String usageId;
        if (userQuantityUsageExists) {
            // 添加信号量许可数量
            userQuantityUsageSemaphore.addPermits
                    // 只支持 int 类型
                            (Integer.parseInt(orderQuantity));
            // 更新用户接口计数用法的库存量
            userQuantityUsageMapper.updateStockByCreationDTO(creationDTO);
            // 原来的用法主键
            usageId = originalUserQuantityUsagePO.getId();
        } else {
            // 创建新的信号量
            userQuantityUsageSemaphore.trySetPermits
                    (Integer.parseInt(orderQuantity));
            // 保存新的用户接口计数用法
            UserQuantityUsagePO newUserQuantityUsagePO = new UserQuantityUsagePO();
            newUserQuantityUsagePO.setAccountId(accountId);
            newUserQuantityUsagePO.setDigestId(digestId);
            newUserQuantityUsagePO.setTotal(orderQuantity);
            userQuantityUsageMapper.insert(newUserQuantityUsagePO);
            // 新的用法主键
            usageId = newUserQuantityUsagePO.getId();
        }
        return usageId;
    }

}
