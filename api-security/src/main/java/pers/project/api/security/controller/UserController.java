package pers.project.api.security.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import pers.project.api.common.constant.enumeration.ErrorEnum;
import pers.project.api.common.exception.ServiceException;
import pers.project.api.common.model.Response;
import pers.project.api.common.model.entity.UserEntity;
import pers.project.api.common.util.ResponseUtils;
import pers.project.api.security.model.data.UserData;
import pers.project.api.security.model.request.UserLoginRequest;
import pers.project.api.security.model.request.UserRegisterRequest;
import pers.project.api.security.service.UserService;

/**
 * 用户接口
 *
 * @author yupi
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    // region 登录相关

    /**
     * 用户注册
     *
     * @param userRegisterRequest
     * @return
     */
    @PostMapping("/register")
    public Response<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            throw new ServiceException(ErrorEnum.PARAMS_ERROR);
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            return null;
        }
        long result = userService.userRegister(userAccount, userPassword, checkPassword);
        return ResponseUtils.success(result);
    }

    /**
     * 用户登录
     *
     * @param userLoginRequest
     * @param request
     * @return
     */
    @PostMapping("/login")
    public Response<UserEntity> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) {
            throw new ServiceException(ErrorEnum.PARAMS_ERROR);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new ServiceException(ErrorEnum.PARAMS_ERROR);
        }
        UserEntity userEntity = userService.userLogin(userAccount, userPassword, request);
        return ResponseUtils.success(userEntity);
    }

    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public Response<Boolean> userLogout(HttpServletRequest request) {
        if (request == null) {
            throw new ServiceException(ErrorEnum.PARAMS_ERROR);
        }
        boolean result = userService.userLogout(request);
        return ResponseUtils.success(result);
    }

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    @GetMapping("/get/login")
    public Response<UserData> getLoginUser(HttpServletRequest request) {
        UserEntity userEntity = userService.getLoginUser(request);
        UserData userData = new UserData();
        BeanUtils.copyProperties(userEntity, userData);
        return ResponseUtils.success(userData);
    }

    // endregion

    // /user
    @GetMapping("/getInvokeUser")
    public Response<UserEntity> getInvokeUser(@RequestParam("accessKey") String accessKey) {
        return ResponseUtils.success(userService.getInvokeUser(accessKey));
    }

}
