package pers.project.api.facade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ArrayUtils;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pers.project.api.common.model.dto.UserQuantityUsageCreationDTO;
import pers.project.api.common.model.query.ApiAdminPageQuery;
import pers.project.api.common.model.query.UserApiDigestPageQuery;
import pers.project.api.common.model.query.UserApiFormatAndQuantityUsageQuery;
import pers.project.api.common.model.vo.*;
import pers.project.api.common.util.BeanCopierUtils;
import pers.project.api.facade.mapper.*;
import pers.project.api.facade.model.po.ApiDigestPo;
import pers.project.api.facade.model.po.ApiFormatPO;
import pers.project.api.facade.model.po.UserQuantityUsagePO;
import pers.project.api.facade.service.FacadeService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.util.StringUtils.hasText;

/**
 * Facade 模块的 Service 实现
 *
 * @author Luo Fei
 * @date 2023/05/05
 */
@Service
@RequiredArgsConstructor
public class FacadeServiceImpl implements FacadeService {

    private final ApiDigestMapper apiDigestMapper;

    private final ApiFormatMapper apiFormatMapper;

    private final FacadeMapper facadeMapper;

    private final ApiQuantityUsageMapper apiQuantityUsageMapper;

    private final UserQuantityUsageMapper userQuantityUsageMapper;

    @Override
    public UserApiDigestPageVO getUserApiDigestPageDTO(UserApiDigestPageQuery pageQuery) {
        // 按 Query 条件进行分页查询
        LambdaQueryWrapper<ApiDigestPo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(ApiDigestPo::getId, ApiDigestPo::getApiName, ApiDigestPo::getDescription,
                ApiDigestPo::getMethod, ApiDigestPo::getUrl, ApiDigestPo::getUsageType,
                ApiDigestPo::getApiStatus, ApiDigestPo::getCreateTime, ApiDigestPo::getUpdateTime);
        queryWrapper.eq(ApiDigestPo::getAccountId, pageQuery.getAccountId());
        queryWrapper.like(hasText(pageQuery.getApiName()),
                ApiDigestPo::getApiName, pageQuery.getApiName());
        queryWrapper.like(hasText(pageQuery.getDescription()),
                ApiDigestPo::getDescription, pageQuery.getDescription());
        queryWrapper.in((CollectionUtils.isNotEmpty(pageQuery.getMethod())),
                ApiDigestPo::getApiStatus, pageQuery.getMethod());
        queryWrapper.like(hasText(pageQuery.getUrl()),
                ApiDigestPo::getUrl, pageQuery.getUrl());
        queryWrapper.in(CollectionUtils.isNotEmpty(pageQuery.getUsageType()),
                ApiDigestPo::getApiStatus, pageQuery.getUsageType());
        queryWrapper.in(CollectionUtils.isNotEmpty(pageQuery.getApiStatus()),
                ApiDigestPo::getApiStatus, pageQuery.getApiStatus());
        LocalDateTime[] createTime = pageQuery.getCreateTime();
        queryWrapper.and(ArrayUtils.isNotEmpty(createTime),
                wrapper -> wrapper.ge(ApiDigestPo::getCreateTime, createTime[0])
                        .le(ApiDigestPo::getCreateTime, createTime[1]));
        LocalDateTime[] updateTime = pageQuery.getUpdateTime();
        queryWrapper.and(ArrayUtils.isNotEmpty(updateTime),
                wrapper -> wrapper.ge(ApiDigestPo::getUpdateTime, updateTime[0])
                        .le(ApiDigestPo::getUpdateTime, updateTime[1]));
        Page<ApiDigestPo> page = apiDigestMapper.selectPage
                (Page.of(pageQuery.getCurrent(), pageQuery.getSize()), queryWrapper);
        // 转换查询到的分页数据
        List<UserApiDigestVO> userApiDigestVOList
                = page.getRecords().stream().map(apiDigestPo -> {
            UserApiDigestVO userApiDigestVO = new UserApiDigestVO();
            BeanCopierUtils.copy(apiDigestPo, userApiDigestVO);
            userApiDigestVO.setDigestId(apiDigestPo.getId());
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
    // TODO: 2023/6/5 分布式事务
//    @Transactional(rollbackFor = Throwable.class)
    public String createUserQuantityUsage(UserQuantityUsageCreationDTO creationDTO) {
        LambdaQueryWrapper<UserQuantityUsagePO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(UserQuantityUsagePO::getId);
        String accountId = creationDTO.getAccountId();
        queryWrapper.eq(UserQuantityUsagePO::getAccountId, accountId);
        String digestId = creationDTO.getDigestId();
        queryWrapper.eq(UserQuantityUsagePO::getDigestId, digestId);
        UserQuantityUsagePO originalUsagePO = userQuantityUsageMapper.selectOne(queryWrapper);
        String orderQuantity = creationDTO.getOrderQuantity();
        String usageId;
        if (originalUsagePO != null) {
            usageId = originalUsagePO.getId();
            userQuantityUsageMapper.updateStock(usageId, orderQuantity);
        } else {
            UserQuantityUsagePO userQuantityUsagePO = new UserQuantityUsagePO();
            userQuantityUsagePO.setAccountId(accountId);
            userQuantityUsagePO.setDigestId(digestId);
            userQuantityUsagePO.setTotal(orderQuantity);
            userQuantityUsageMapper.insert(userQuantityUsagePO);
            usageId = userQuantityUsagePO.getId();
        }
        // TODO: 2023/6/6 必须消峰，定时扣减库存量
        apiQuantityUsageMapper.updateStock(digestId, orderQuantity);
        return usageId;
    }

}
