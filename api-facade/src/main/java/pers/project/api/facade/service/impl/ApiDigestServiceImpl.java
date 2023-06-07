package pers.project.api.facade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import pers.project.api.common.util.BeanCopierUtils;
import pers.project.api.facade.mapper.ApiDigestMapper;
import pers.project.api.facade.model.po.ApiDigestPo;
import pers.project.api.facade.model.query.ApiDigestPageQuery;
import pers.project.api.facade.model.vo.ApiDigestPageVO;
import pers.project.api.facade.model.vo.ApiDigestVO;
import pers.project.api.facade.service.ApiDigestService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static org.springframework.util.StringUtils.collectionToCommaDelimitedString;
import static org.springframework.util.StringUtils.commaDelimitedListToSet;

/**
 * 针对表【api_digest (接口摘要) 】的数据库操作 Service 实现
 *
 * @author Luo Fei
 * @date 2023/05/04
 */
@Service
public class ApiDigestServiceImpl extends ServiceImpl<ApiDigestMapper, ApiDigestPo> implements ApiDigestService {

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
                wrapper -> wrapper.ge(ApiDigestPo::getCreateTime, createTimeRange[0])
                        .le(ApiDigestPo::getCreateTime, createTimeRange[1]));
        LocalDateTime[] updateTimeRange = pageQuery.getUpdateTimeRange();
        queryWrapper.and(nonNull(updateTimeRange),
                wrapper -> wrapper.ge(ApiDigestPo::getUpdateTime, updateTimeRange[0])
                        .le(ApiDigestPo::getUpdateTime, updateTimeRange[1]));
        // 用 QueryWrapper 分页查询
        Page<ApiDigestPo> page = page
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

}




