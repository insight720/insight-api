package pers.project.api.facade.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.project.api.common.model.Result;
import pers.project.api.common.util.ResultUtils;
import pers.project.api.common.validation.constraint.SnowflakeId;
import pers.project.api.facade.model.vo.ApiQuantityUsageVO;
import pers.project.api.facade.model.vo.ApiStockInfoVO;
import pers.project.api.facade.service.ApiQuantityUsageService;

/**
 * 接口计数用法控制器
 *
 * @author Luo Fei
 * @date 2023/06/01
 */
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/quantity/usage")
public class ApiQuantityUsageController {

    private final ApiQuantityUsageService apiQuantityUsageService;

    @GetMapping("/{digestId}")
    public Result<ApiQuantityUsageVO> viewApiQuantityUsage(@SnowflakeId @PathVariable String digestId) {
        ApiQuantityUsageVO apiQuantityUsageVO = apiQuantityUsageService.getApiQuantityUsageVO(digestId);
        return ResultUtils.success(apiQuantityUsageVO);
    }

    @GetMapping("/stock/info/{digestId}")
    public Result<ApiStockInfoVO> viewApiStockInfo(@SnowflakeId @PathVariable String digestId) {
        ApiStockInfoVO apiStockInfoVO = apiQuantityUsageService.getApiStockInfoVO(digestId);
        return ResultUtils.success(apiStockInfoVO);
    }

}
