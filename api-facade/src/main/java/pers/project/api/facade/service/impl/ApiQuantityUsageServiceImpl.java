package pers.project.api.facade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import pers.project.api.common.util.BeanCopierUtils;
import pers.project.api.facade.mapper.ApiQuantityUsageMapper;
import pers.project.api.facade.model.po.ApiQuantityUsagePO;
import pers.project.api.facade.model.vo.ApiQuantityUsageVO;
import pers.project.api.facade.model.vo.ApiStockInfoVO;
import pers.project.api.facade.service.ApiQuantityUsageService;

import java.util.concurrent.CompletableFuture;

import static pers.project.api.common.constant.redis.RedissonNamePrefixConst.API_QUANTITY_USAGE_FAILURE_SEMAPHORE_NAME_PREFIX;
import static pers.project.api.common.constant.redis.RedissonNamePrefixConst.API_QUANTITY_USAGE_TOTAL_SEMAPHORE_NAME_PREFIX;

/**
 * 针对表【api_quantity_usage (接口计数用法) 】的数据库操作 Service 实现
 *
 * @author Luo Fei
 * @date 2023/05/04
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ApiQuantityUsageServiceImpl extends ServiceImpl<ApiQuantityUsageMapper, ApiQuantityUsagePO> implements ApiQuantityUsageService {

    private final RedissonClient redissonClient;

    @Override
    public ApiQuantityUsageVO getApiQuantityUsageVO(String digestId) {
        // 查询接口调用次数统计量和失败调用次数统计量
        String apiTotalSemaphoreName = API_QUANTITY_USAGE_TOTAL_SEMAPHORE_NAME_PREFIX + digestId;
        int total = redissonClient.getSemaphore(apiTotalSemaphoreName).availablePermits();
        String apiFailureSemaphoreName = API_QUANTITY_USAGE_FAILURE_SEMAPHORE_NAME_PREFIX + digestId;
        int failure = redissonClient.getSemaphore(apiFailureSemaphoreName).availablePermits();
        // 查询后更新数据库（临时写法）
        // TODO: 2023/7/16 临时写法
        CompletableFuture.runAsync(() -> {
            LambdaUpdateWrapper<ApiQuantityUsagePO> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.set(ApiQuantityUsagePO::getTotal, total);
            updateWrapper.set(ApiQuantityUsagePO::getFailure, failure);
            updateWrapper.eq(ApiQuantityUsagePO::getDigestId, digestId);
            update(updateWrapper);
        }).exceptionally(throwable -> {
            log.warn("Error update api quantity usage, digestId: {}, exception message: {}",
                    digestId, throwable.getMessage());
            return null;
        });
        LambdaQueryWrapper<ApiQuantityUsagePO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(ApiQuantityUsagePO::getStock, ApiQuantityUsagePO::getLockedStock,
                ApiQuantityUsagePO::getUsageStatus, ApiQuantityUsagePO::getCreateTime,
                ApiQuantityUsagePO::getUpdateTime);
        queryWrapper.eq(ApiQuantityUsagePO::getDigestId, digestId);
        ApiQuantityUsagePO apiQuantityUsagePO = getOne(queryWrapper);
        ApiQuantityUsageVO apiQuantityUsageVO = new ApiQuantityUsageVO();
        BeanCopierUtils.copy(apiQuantityUsagePO, apiQuantityUsageVO);
        apiQuantityUsageVO.setTotal((long) total);
        apiQuantityUsageVO.setFailure((long) failure);
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




