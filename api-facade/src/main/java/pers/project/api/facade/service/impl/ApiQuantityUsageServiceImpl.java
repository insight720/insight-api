package pers.project.api.facade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import pers.project.api.common.util.BeanCopierUtils;
import pers.project.api.facade.mapper.ApiQuantityUsageMapper;
import pers.project.api.facade.model.po.ApiQuantityUsagePO;
import pers.project.api.facade.model.vo.ApiQuantityUsageVO;
import pers.project.api.facade.model.vo.ApiStockInfoVO;
import pers.project.api.facade.service.ApiQuantityUsageService;

/**
 * 针对表【api_quantity_usage (接口计数用法) 】的数据库操作 Service 实现
 *
 * @author Luo Fei
 * @date 2023/05/04
 */
@Service
public class ApiQuantityUsageServiceImpl extends ServiceImpl<ApiQuantityUsageMapper, ApiQuantityUsagePO> implements ApiQuantityUsageService {

    @Override
    public ApiQuantityUsageVO getApiQuantityUsageVO(String digestId) {
        LambdaQueryWrapper<ApiQuantityUsagePO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(ApiQuantityUsagePO::getTotal, ApiQuantityUsagePO::getFailure,
                ApiQuantityUsagePO::getStock, ApiQuantityUsagePO::getLockedStock,
                ApiQuantityUsagePO::getUsageStatus, ApiQuantityUsagePO::getCreateTime,
                ApiQuantityUsagePO::getUpdateTime);
        queryWrapper.eq(ApiQuantityUsagePO::getDigestId, digestId);
        ApiQuantityUsagePO apiQuantityUsagePO = getOne(queryWrapper);
        ApiQuantityUsageVO apiQuantityUsageVO = new ApiQuantityUsageVO();
        BeanCopierUtils.copy(apiQuantityUsagePO, apiQuantityUsageVO);
        return apiQuantityUsageVO;
    }

    @Override
    public ApiStockInfoVO getApiStockInfoVO(String digestId) {
        LambdaQueryWrapper<ApiQuantityUsagePO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(ApiQuantityUsagePO::getStock, ApiQuantityUsagePO::getLockedStock,
                ApiQuantityUsagePO::getUsageStatus, ApiQuantityUsagePO::getUpdateTime);
        queryWrapper.eq(ApiQuantityUsagePO::getDigestId, digestId);
        ApiQuantityUsagePO apiQuantityUsagePO = getOne(queryWrapper);
        ApiStockInfoVO apiStockInfoVO = new ApiStockInfoVO();
        BeanCopierUtils.copy(apiQuantityUsagePO, apiStockInfoVO);
        return apiStockInfoVO;
    }

}




