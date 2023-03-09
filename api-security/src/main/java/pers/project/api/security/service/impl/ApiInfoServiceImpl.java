package pers.project.api.security.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import pers.project.api.security.common.ErrorCode;
import pers.project.api.security.exception.BusinessException;
import pers.project.api.security.mapper.ApiInfoMapper;
import pers.project.api.security.model.entity.ApiInfo;
import pers.project.api.security.service.ApiInfoService;

/**
 * 针对表【api_info (接口信息) 】的数据库操作 Service 实现
 *
 * @author Luo Fei
 * @date 2023-02-22
 */
@Service
public class ApiInfoServiceImpl extends ServiceImpl<ApiInfoMapper, ApiInfo> implements ApiInfoService {

    @Override
    public void validApiInfo(ApiInfo apiInfo, boolean add) {
        // TODO: 2023/2/22 校验
    }

    @Override
    public ApiInfo getApiInfo(String url, String method) {
        if (StringUtils.isAnyBlank(url, method)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<ApiInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("url", url);
        queryWrapper.eq("method", method);
        return baseMapper.selectOne(queryWrapper);
    }

}




