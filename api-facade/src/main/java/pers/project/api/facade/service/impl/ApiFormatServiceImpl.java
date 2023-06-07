package pers.project.api.facade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import pers.project.api.common.util.BeanCopierUtils;
import pers.project.api.facade.mapper.ApiFormatMapper;
import pers.project.api.facade.model.po.ApiFormatPO;
import pers.project.api.facade.model.vo.ApiFormatVO;
import pers.project.api.facade.service.ApiFormatService;

import static java.util.Objects.nonNull;

/**
 * 针对表【api_format (接口格式) 】的数据库操作 Service 实现
 *
 * @author Luo Fei
 * @date 2023/05/04
 */
@Service
public class ApiFormatServiceImpl extends ServiceImpl<ApiFormatMapper, ApiFormatPO> implements ApiFormatService {

    @Override
    public ApiFormatVO getApiFormatVO(String digestId) {
        // 查询 ApiFormat 信息
        LambdaQueryWrapper<ApiFormatPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(ApiFormatPO::getRequestParam, ApiFormatPO::getRequestHeader,
                ApiFormatPO::getRequestBody, ApiFormatPO::getResponseHeader,
                ApiFormatPO::getResponseBody, ApiFormatPO::getCreateTime,
                ApiFormatPO::getUpdateTime);
        queryWrapper.eq(ApiFormatPO::getDigestId, digestId);
        ApiFormatPO apiFormatPO = getOne(queryWrapper);
        // 复制属性
        ApiFormatVO apiFormatVO = new ApiFormatVO();
        if (nonNull(apiFormatPO)) {
            BeanCopierUtils.copy(apiFormatPO, apiFormatVO);
        }
        return apiFormatVO;
    }

}




