package pers.project.api.security.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.project.api.common.model.Result;
import pers.project.api.common.util.ResultUtils;
import pers.project.api.security.model.dto.QuantityUsageOrderCancellationDTO;
import pers.project.api.security.model.dto.QuantityUsageOrderConfirmationDTO;
import pers.project.api.security.model.dto.QuantityUsageOrderCreationDTO;
import pers.project.api.security.model.query.QuantityUsageOrderPageQuery;
import pers.project.api.security.model.vo.QuantityUsageOrderPageVO;
import pers.project.api.security.service.QuantityUsageOrderService;

/**
 * 接口计数用法订单控制器
 *
 * @author Luo Fei
 * @date 2023/07/08
 */
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/order")
public class QuantityUsageOrderController {

    private final QuantityUsageOrderService userOrderService;

    @PostMapping("/page")
    public Result<QuantityUsageOrderPageVO> viewQuantityUsageOrderPage
            (@Valid @RequestBody QuantityUsageOrderPageQuery pageQuery) {
        QuantityUsageOrderPageVO pageVO = userOrderService.getQuantityUsageOrderPageVO(pageQuery);
        return ResultUtils.success(pageVO);
    }

    @PostMapping("/quantity/usage/creation")
    public Result<Void> placeQuantityUsageOrder
            (@Valid @RequestBody QuantityUsageOrderCreationDTO creationDTO) {
        userOrderService.createQuantityUsageOrder(creationDTO);
        return ResultUtils.success();
    }

    @PostMapping("/quantity/usage/cancellation")
    public Result<Void> cancelQuantityUsageOrder
            (@Valid @RequestBody QuantityUsageOrderCancellationDTO cancellationDTO) {
        userOrderService.cancelQuantityUsageOrder(cancellationDTO);
        return ResultUtils.success();
    }

    @PostMapping("/quantity/usage/confirmation")
    public Result<Void> confirmQuantityUsageOrder
            (@Valid @RequestBody QuantityUsageOrderConfirmationDTO orderConfirmationDTO) {
        userOrderService.confirmQuantityUsageOrder(orderConfirmationDTO);
        return ResultUtils.success();
    }

}
