package pers.project.api.facade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ArrayUtils;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pers.project.api.common.model.query.ApiAdminPageQuery;
import pers.project.api.common.model.query.UserApiDigestPageQuery;
import pers.project.api.common.model.query.UserApiFormatAndQuantityUsageQuery;
import pers.project.api.common.model.vo.*;
import pers.project.api.common.util.BeanCopierUtils;
import pers.project.api.facade.mapper.ApiDigestMapper;
import pers.project.api.facade.mapper.ApiFormatMapper;
import pers.project.api.facade.mapper.FacadeMapper;
import pers.project.api.facade.mapper.UserQuantityUsageMapper;
import pers.project.api.facade.model.po.ApiDigestPo;
import pers.project.api.facade.model.po.ApiFormatPo;
import pers.project.api.facade.model.po.UserQuantityUsagePo;
import pers.project.api.facade.model.query.ApiDigestPageQuery;
import pers.project.api.facade.model.vo.ApiDigestPageVO;
import pers.project.api.facade.model.vo.ApiDigestVO;
import pers.project.api.facade.service.FacadeService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static org.springframework.util.StringUtils.*;

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

    private final UserQuantityUsageMapper userQuantityUsageMapper;

    @Override
    public ApiDigestPageVO getApiDigestPageVO(ApiDigestPageQuery pageQuery) {
        // 按 Query 条件组装 QueryWrapper
        LambdaQueryWrapper<ApiDigestPo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(ApiDigestPo::getId, ApiDigestPo::getAccountId, ApiDigestPo::getApiName,
                ApiDigestPo::getDescription, ApiDigestPo::getMethod, ApiDigestPo::getUrl,
                ApiDigestPo::getApiStatus, ApiDigestPo::getUsageType, ApiDigestPo::getCreateTime,
                ApiDigestPo::getUpdateTime);
        String apiName = pageQuery.getApiName();
        queryWrapper.like(nonNull(apiName), ApiDigestPo::getApiName, apiName);
        String description = pageQuery.getDescription();
        queryWrapper.like(nonNull(description), ApiDigestPo::getDescription, description);
        Set<String> methodSet = pageQuery.getMethodSet();
        queryWrapper.like(nonNull(methodSet), ApiDigestPo::getMethod,
                collectionToCommaDelimitedString(methodSet));
        String url = pageQuery.getUrl();
        queryWrapper.like(nonNull(url), ApiDigestPo::getUrl, url);
        Set<String> usageTypeSet = pageQuery.getUsageTypeSet();
        queryWrapper.in(nonNull(usageTypeSet), ApiDigestPo::getUsageType,
                collectionToCommaDelimitedString(usageTypeSet));
        Set<Integer> apiStatusSet = pageQuery.getApiStatusSet();
        queryWrapper.in(nonNull(apiStatusSet), ApiDigestPo::getApiStatus, apiStatusSet);
        LocalDateTime[] createTimeRange = pageQuery.getCreateTimeRange();
        queryWrapper.and(nonNull(createTimeRange),
                wrapper -> wrapper.ge(ApiDigestPo::getCreateTime, createTimeRange[1])
                        .le(ApiDigestPo::getCreateTime, createTimeRange[2]));
        LocalDateTime[] updateTimeRange = pageQuery.getUpdateTimeRange();
        queryWrapper.and(nonNull(updateTimeRange),
                wrapper -> wrapper.ge(ApiDigestPo::getUpdateTime, updateTimeRange[1])
                        .le(ApiDigestPo::getUpdateTime, updateTimeRange[2]));
        // 用 QueryWrapper 分页查询
        Page<ApiDigestPo> page = apiDigestMapper.selectPage
                (Page.of(pageQuery.getCurrent(), pageQuery.getSize()), queryWrapper);
        ApiDigestPageVO apiDigestPageVO = new ApiDigestPageVO();
        apiDigestPageVO.setTotal(page.getTotal());
        List<ApiDigestVO> apiDigestVOList = page.getRecords().stream()
                .map(apiDigestPo -> {
                    ApiDigestVO apiDigestVO = new ApiDigestVO();
                    BeanCopierUtils.copy(apiDigestPo, apiDigestVO);
                    apiDigestVO.setDigestId(apiDigestPo.getId());
                    apiDigestVO.setUsageTypeSet
                            (commaDelimitedListToSet(apiDigestPo.getUsageType()));
                    apiDigestVO.setMethodSet
                            (commaDelimitedListToSet(apiDigestPo.getMethod()));
                    return apiDigestVO;
                }).collect(Collectors.toList());
        apiDigestPageVO.setDigestVOList(apiDigestVOList);
        return apiDigestPageVO;
    }

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
        LambdaQueryWrapper<ApiFormatPo> formatQueryWrapper = new LambdaQueryWrapper<>();
        formatQueryWrapper.select(ApiFormatPo::getRequestParam, ApiFormatPo::getRequestHeader,
                ApiFormatPo::getRequestBody, ApiFormatPo::getResponseHeader, ApiFormatPo::getResponseBody);
        formatQueryWrapper.eq(ApiFormatPo::getDigestId, query.getDigestId());
        ApiFormatPo apiFormatPO = apiFormatMapper.selectOne(formatQueryWrapper);
        BeanCopierUtils.copy(apiFormatPO, formatAndQuantityUsageVO);
        // 查询 API 计数用法信息
        LambdaQueryWrapper<UserQuantityUsagePo> usageQueryWrapper = new LambdaQueryWrapper<>();
        usageQueryWrapper.select(UserQuantityUsagePo::getTotal, UserQuantityUsagePo::getFailure,
                UserQuantityUsagePo::getStock, UserQuantityUsagePo::getUsageStatus);
        usageQueryWrapper.eq(UserQuantityUsagePo::getAccountId, query.getAccountId());
        usageQueryWrapper.eq(UserQuantityUsagePo::getDigestId, query.getDigestId());
        UserQuantityUsagePo userQuantityUsagePo = userQuantityUsageMapper.selectOne(usageQueryWrapper);
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

}
