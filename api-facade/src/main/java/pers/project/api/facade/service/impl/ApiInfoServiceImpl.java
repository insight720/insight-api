package pers.project.api.facade.service.impl;

import com.alibaba.nacos.common.utils.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import pers.project.api.common.common.ErrorCode;
import pers.project.api.common.exception.BusinessException;
import pers.project.api.common.model.entity.ApiInfo;
import pers.project.api.facade.mapper.ApiInfoMapper;
import pers.project.api.facade.service.ApiInfoService;


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




