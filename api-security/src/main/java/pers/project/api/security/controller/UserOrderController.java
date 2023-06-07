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
import pers.project.api.security.model.dto.QuantityUsageOrderCreationDTO;
import pers.project.api.security.model.query.UserOrderPageQuery;
import pers.project.api.security.model.vo.UserOrderPageVO;
import pers.project.api.security.service.UserOrderService;

/**
 * 用户订单控制器
 *
 * @author Luo Fei
 * @date 2023/05/16
 */
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/order")
public class UserOrderController {

    private final UserOrderService userOrderService;

    @PostMapping("/page")
    public Result<UserOrderPageVO> viewUserOrderPage
            (@Valid @RequestBody UserOrderPageQuery pageQuery) {
        UserOrderPageVO pageVO = userOrderService.getUserOrderPageVO(pageQuery);
        return ResultUtils.success(pageVO);
    }

    @PostMapping("/quantity/usage/creation")
    public Result<Void> placeQuantityUsageOrder
            (@Valid @RequestBody QuantityUsageOrderCreationDTO creationDTO) {
        userOrderService.createQuantityUsageOrder(creationDTO);
        return ResultUtils.success();
    }

}
