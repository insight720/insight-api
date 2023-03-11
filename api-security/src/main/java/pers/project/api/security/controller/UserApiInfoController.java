package pers.project.api.security.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pers.project.api.security.annotation.AuthCheck;
import pers.project.api.security.common.BaseResponse;
import pers.project.api.security.common.ErrorCode;
import pers.project.api.security.common.ResultUtils;
import pers.project.api.security.constant.CommonConstant;
import pers.project.api.security.constant.UserConstant;
import pers.project.api.security.exception.BusinessException;
import pers.project.api.security.model.dto.userapiinfo.UserApiInfoQueryRequest;
import pers.project.api.security.model.entity.UserApiInfo;
import pers.project.api.security.service.UserApiInfoService;

/**
 * 帖子接口
 *
 * @author yupi
 */
@RestController
@RequestMapping("/userApiInfo")
@Slf4j
public class UserApiInfoController {

    @Resource
    private UserApiInfoService userApiInfoService;

    /**
     * 分页获取列表
     *
     * @param userApiInfoQueryRequest
     * @param request
     * @return
     */
    @GetMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<UserApiInfo>> listUserApiInfoByPage(UserApiInfoQueryRequest userApiInfoQueryRequest, HttpServletRequest request) {
        if (userApiInfoQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserApiInfo userApiInfoQuery = new UserApiInfo();
        BeanUtils.copyProperties(userApiInfoQueryRequest, userApiInfoQuery);
        long current = userApiInfoQueryRequest.getCurrent();
        long size = userApiInfoQueryRequest.getPageSize();
        String sortField = userApiInfoQueryRequest.getSortField();
        String sortOrder = userApiInfoQueryRequest.getSortOrder();
        // 限制爬虫
        if (size > 50) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<UserApiInfo> queryWrapper = new QueryWrapper<>(userApiInfoQuery);
        queryWrapper.orderBy(StringUtils.isNotBlank(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC), sortField);
        Page<UserApiInfo> userApiInfoPage = userApiInfoService.page(new Page<>(current, size), queryWrapper);
        return ResultUtils.success(userApiInfoPage);
    }

    // endregion

    // /userApiInfo
    @GetMapping("/invokeCount")
    public BaseResponse<Boolean> invokeCount(@RequestParam("apiInfoId") long apiInfoId,
                                             @RequestParam("userId") long userId) {
        return ResultUtils.success(userApiInfoService.invokeCount(apiInfoId, userId));
    }

}
