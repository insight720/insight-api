package pers.project.api.security.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import pers.project.api.security.annotation.AuthCheck;
import pers.project.api.security.common.BaseResponse;
import pers.project.api.security.common.DeleteRequest;
import pers.project.api.security.common.ErrorCode;
import pers.project.api.security.common.ResultUtils;
import pers.project.api.security.constant.CommonConstant;
import pers.project.api.security.constant.UserConstant;
import pers.project.api.security.exception.BusinessException;
import pers.project.api.security.model.dto.user_api_info.UserApiInfoAddRequest;
import pers.project.api.security.model.dto.user_api_info.UserApiInfoQueryRequest;
import pers.project.api.security.model.dto.user_api_info.UserApiInfoUpdateRequest;
import pers.project.api.security.model.entity.User;
import pers.project.api.security.model.entity.UserApiInfo;
import pers.project.api.security.service.UserApiInfoService;
import pers.project.api.security.service.UserService;

import java.util.List;

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

    @Resource
    private UserService userService;

    // region 增删改查

    /**
     * 创建
     *
     * @param userApiInfoAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addUserApiInfo(@RequestBody UserApiInfoAddRequest userApiInfoAddRequest, HttpServletRequest request) {
        if (userApiInfoAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserApiInfo userApiInfo = new UserApiInfo();
        BeanUtils.copyProperties(userApiInfoAddRequest, userApiInfo);
        // 校验
        userApiInfoService.validUserApiInfo(userApiInfo, true);
        User loginUser = userService.getLoginUser(request);
        userApiInfo.setUserId(loginUser.getId());
        boolean result = userApiInfoService.save(userApiInfo);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        long newUserApiInfoId = userApiInfo.getId();
        return ResultUtils.success(newUserApiInfoId);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteUserApiInfo(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        UserApiInfo oldUserApiInfo = userApiInfoService.getById(id);
        if (oldUserApiInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 仅本人或管理员可删除
        if (!oldUserApiInfo.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = userApiInfoService.removeById(id);
        return ResultUtils.success(result);
    }

    /**
     * 更新
     *
     * @param userApiInfoUpdateRequest
     * @param request
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateUserApiInfo(@RequestBody UserApiInfoUpdateRequest userApiInfoUpdateRequest,
                                                   HttpServletRequest request) {
        if (userApiInfoUpdateRequest == null || userApiInfoUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserApiInfo userApiInfo = new UserApiInfo();
        BeanUtils.copyProperties(userApiInfoUpdateRequest, userApiInfo);
        // 参数校验
        userApiInfoService.validUserApiInfo(userApiInfo, false);
        User user = userService.getLoginUser(request);
        long id = userApiInfoUpdateRequest.getId();
        // 判断是否存在
        UserApiInfo oldUserApiInfo = userApiInfoService.getById(id);
        if (oldUserApiInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 仅本人或管理员可修改
        if (!oldUserApiInfo.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = userApiInfoService.updateById(userApiInfo);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<UserApiInfo> getUserApiInfoById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserApiInfo userApiInfo = userApiInfoService.getById(id);
        return ResultUtils.success(userApiInfo);
    }

    /**
     * 获取列表（仅管理员可使用）
     *
     * @param userApiInfoQueryRequest
     * @return
     */
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @GetMapping("/list")
    public BaseResponse<List<UserApiInfo>> listUserApiInfo(UserApiInfoQueryRequest userApiInfoQueryRequest) {
        UserApiInfo userApiInfoQuery = new UserApiInfo();
        if (userApiInfoQueryRequest != null) {
            BeanUtils.copyProperties(userApiInfoQueryRequest, userApiInfoQuery);
        }
        QueryWrapper<UserApiInfo> queryWrapper = new QueryWrapper<>(userApiInfoQuery);
        List<UserApiInfo> userApiInfoList = userApiInfoService.list(queryWrapper);
        return ResultUtils.success(userApiInfoList);
    }

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
                                             @RequestParam("userId") long userId)  {
        return ResultUtils.success(userApiInfoService.invokeCount(apiInfoId, userId));
    }

}
