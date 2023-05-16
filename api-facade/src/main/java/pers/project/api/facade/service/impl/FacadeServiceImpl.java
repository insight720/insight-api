package pers.project.api.facade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pers.project.api.common.model.dto.UserApiDigestPageVO;
import pers.project.api.common.model.query.UserApiDigestPageQuery;
import pers.project.api.common.model.query.UserApiFormatAndQuantityUsageQuery;
import pers.project.api.common.model.vo.UserApiDigestVO;
import pers.project.api.common.model.vo.UserApiFormatAndQuantityUsageVO;
import pers.project.api.common.util.BeanCopierUtils;
import pers.project.api.facade.mapper.ApiFormatMapper;
import pers.project.api.facade.mapper.UserQuantityUsageMapper;
import pers.project.api.facade.model.po.ApiDigestPo;
import pers.project.api.facade.model.po.ApiFormatPo;
import pers.project.api.facade.model.po.UserQuantityUsagePo;
import pers.project.api.facade.service.ApiDigestService;
import pers.project.api.facade.service.FacadeService;

import java.util.List;
import java.util.stream.Collectors;

import static com.baomidou.mybatisplus.core.toolkit.CollectionUtils.isNotEmpty;
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

    private final ApiDigestService apiDigestService;

    private final ApiFormatMapper apiFormatMapper;

    private final UserQuantityUsageMapper userQuantityUsageMapper;

    @Override
    public UserApiDigestPageVO getUserApiDigestPageDTO(UserApiDigestPageQuery pageQuery) {
        // 按 Query 条件进行分页查询
        LambdaQueryWrapper<ApiDigestPo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(ApiDigestPo::getId, ApiDigestPo::getApiName, ApiDigestPo::getDescription,
                ApiDigestPo::getMethod, ApiDigestPo::getUrl, ApiDigestPo::getApiStatus,
                ApiDigestPo::getCreateTime, ApiDigestPo::getUpdateTime);
        queryWrapper.eq(ApiDigestPo::getAccountId, pageQuery.getAccountId());
        queryWrapper.like(hasText(pageQuery.getApiName()),
                ApiDigestPo::getApiName, pageQuery.getApiName());
        queryWrapper.like(hasText(pageQuery.getDescription()),
                ApiDigestPo::getDescription, pageQuery.getDescription());
        queryWrapper.in(isNotEmpty(pageQuery.getMethod()),
                ApiDigestPo::getApiStatus, pageQuery.getMethod());
        queryWrapper.like(hasText(pageQuery.getUrl()),
                ApiDigestPo::getUrl, pageQuery.getUrl());
        queryWrapper.in(isNotEmpty(pageQuery.getApiStatus()),
                ApiDigestPo::getApiStatus, pageQuery.getApiStatus());
        Page<ApiDigestPo> page = apiDigestService.page
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

}
