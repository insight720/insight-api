package pers.project.api.facade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import pers.project.api.facade.mapper.ApiDigestMapper;
import pers.project.api.facade.model.po.ApiDigestPO;
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
public class ApiDigestServiceImpl extends ServiceImpl<ApiDigestMapper, ApiDigestPO> implements ApiDigestService {

    @Override
    // Suppress warnings for duplicated code lines
    @SuppressWarnings("all")
    public ApiDigestPageVO getApiDigestPageVO(ApiDigestPageQuery pageQuery) {
        // TODO: 2023/7/16 优化查询
        // 按 Query 条件组装 QueryWrapper
        LambdaQueryWrapper<ApiDigestPO> queryWrapper = new LambdaQueryWrapper<>();
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
        Page<ApiDigestPO> page = page
                (Page.of(pageQuery.getCurrent(), pageQuery.getSize()), queryWrapper);
        // 转换查询到的分页数据
        ApiDigestPageVO apiDigestPageVO = new ApiDigestPageVO();
        apiDigestPageVO.setTotal(page.getTotal());
        List<ApiDigestVO> apiDigestVOList = page.getRecords().stream()
                .map(apiDigestPO -> {
                    ApiDigestVO apiDigestVO = new ApiDigestVO();
                    BeanUtils.copyProperties(apiDigestPO, apiDigestVO);
                    apiDigestVO.setDigestId(apiDigestPO.getId());
                    apiDigestVO.setUsageTypeSet
                            (commaDelimitedListToSet(apiDigestPO.getUsageType()));
                    apiDigestVO.setMethodSet
                            (commaDelimitedListToSet(apiDigestPO.getMethod()));
                    return apiDigestVO;
                }).collect(Collectors.toList());
        apiDigestPageVO.setDigestVOList(apiDigestVOList);
        return apiDigestPageVO;
    }

}




