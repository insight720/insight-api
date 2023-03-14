package pers.project.api.facade.controller;

import com.alibaba.nacos.common.utils.StringUtils;
import com.alibaba.nacos.shaded.com.google.gson.Gson;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import pers.project.api.common.constant.CommonConst;
import pers.project.api.common.enums.ErrorCodeEnum;
import pers.project.api.common.exception.ServiceException;
import pers.project.api.common.model.dto.request.DeleteRequest;
import pers.project.api.common.model.dto.request.IdRequest;
import pers.project.api.common.model.dto.response.BaseResponse;
import pers.project.api.common.model.entity.ApiInfo;
import pers.project.api.common.model.entity.User;
import pers.project.api.common.util.ResultUtils;
import pers.project.api.facade.model.dto.apiinfo.ApiInfoAddRequest;
import pers.project.api.facade.model.dto.apiinfo.ApiInfoInvokeRequest;
import pers.project.api.facade.model.dto.apiinfo.ApiInfoQueryRequest;
import pers.project.api.facade.model.dto.apiinfo.ApiInfoUpdateRequest;
import pers.project.api.facade.service.ApiInfoService;
import pers.project.api.sdk.client.TestClient;
import pers.project.api.sdk.model.Test;

import java.util.List;

import static pers.project.api.common.constant.UserConst.USER_LOGIN_STATE;


/**
 * 接口信息控制器
 *
 * @author Luo Fei
 * @date 2023/3/9
 */
@RestController
public class ApiInfoController {

    @Resource
    private ApiInfoService apiInfoService;

    @Resource
    private TestClient testClient;

    // TODO: 2023/3/9 权限校验

    // region 增删改查

    /**
     * 创建
     *
     * @param apiInfoAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addApiInfo(@RequestBody ApiInfoAddRequest apiInfoAddRequest, HttpServletRequest request) {
        if (apiInfoAddRequest == null) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_ERROR);
        }
        ApiInfo apiInfo = new ApiInfo();
        BeanUtils.copyProperties(apiInfoAddRequest, apiInfo);
        // 校验
        apiInfoService.validApiInfo(apiInfo, true);
        User loginUser = getLoginUser(request);
        apiInfo.setUserId(loginUser.getId());
        boolean result = apiInfoService.save(apiInfo);
        if (!result) {
            throw new ServiceException(ErrorCodeEnum.OPERATION_ERROR);
        }
        long newApiInfoId = apiInfo.getId();
        return ResultUtils.success(newApiInfoId);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteApiInfo(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_ERROR);
        }
        User user = getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        ApiInfo oldApiInfo = apiInfoService.getById(id);
        if (oldApiInfo == null) {
            throw new ServiceException(ErrorCodeEnum.NOT_FOUND_ERROR);
        }
        // 仅本人或管理员可删除
        if (!oldApiInfo.getUserId().equals(user.getId())) {
            throw new ServiceException(ErrorCodeEnum.NO_AUTH_ERROR);
        }
        boolean b = apiInfoService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新
     *
     * @param apiInfoUpdateRequest
     * @param request
     * @return
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateApiInfo(@RequestBody ApiInfoUpdateRequest apiInfoUpdateRequest, HttpServletRequest request) {
        if (apiInfoUpdateRequest == null || apiInfoUpdateRequest.getId() <= 0) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_ERROR);
        }
        ApiInfo apiInfo = new ApiInfo();
        BeanUtils.copyProperties(apiInfoUpdateRequest, apiInfo);
        // 参数校验
        apiInfoService.validApiInfo(apiInfo, false);
        User user = getLoginUser(request);
        long id = apiInfoUpdateRequest.getId();
        // 判断是否存在
        ApiInfo oldApiInfo = apiInfoService.getById(id);
        if (oldApiInfo == null) {
            throw new ServiceException(ErrorCodeEnum.NOT_FOUND_ERROR);
        }
        // 仅本人或管理员可修改
        if (!oldApiInfo.getUserId().equals(user.getId())) {
            throw new ServiceException(ErrorCodeEnum.NO_AUTH_ERROR);
        }
        boolean result = apiInfoService.updateById(apiInfo);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<ApiInfo> getApiInfoById(long id) {
        if (id <= 0) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_ERROR);
        }
        ApiInfo apiInfo = apiInfoService.getById(id);
        return ResultUtils.success(apiInfo);
    }

    /**
     * 获取列表（仅管理员可使用）
     *
     * @param apiInfoQueryRequest
     * @return
     */
    @GetMapping("/list")
    public BaseResponse<List<ApiInfo>> listApiInfo(ApiInfoQueryRequest apiInfoQueryRequest) {
        ApiInfo apiInfoQuery = new ApiInfo();
        if (apiInfoQueryRequest != null) {
            BeanUtils.copyProperties(apiInfoQueryRequest, apiInfoQuery);
        }
        QueryWrapper<ApiInfo> queryWrapper = new QueryWrapper<>(apiInfoQuery);
        List<ApiInfo> apiInfoList = apiInfoService.list(queryWrapper);
        return ResultUtils.success(apiInfoList);
    }

    /**
     * 分页获取列表
     *
     * @param apiInfoQueryRequest
     * @param request
     * @return
     */
    @GetMapping("/list/page")
    public BaseResponse<Page<ApiInfo>> listApiInfoByPage(ApiInfoQueryRequest apiInfoQueryRequest, HttpServletRequest request) {
        if (apiInfoQueryRequest == null) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_ERROR);
        }
        ApiInfo apiInfoQuery = new ApiInfo();
        BeanUtils.copyProperties(apiInfoQueryRequest, apiInfoQuery);
        long current = apiInfoQueryRequest.getCurrent();
        long size = apiInfoQueryRequest.getPageSize();
        String sortField = apiInfoQueryRequest.getSortField();
        String sortOrder = apiInfoQueryRequest.getSortOrder();
        String description = apiInfoQuery.getDescription();
        // description 需支持模糊搜索
        apiInfoQuery.setDescription(null);
        // 限制爬虫
        if (size > 50) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_ERROR);
        }
        QueryWrapper<ApiInfo> queryWrapper = new QueryWrapper<>(apiInfoQuery);
        queryWrapper.like(StringUtils.isNotBlank(description), "description", description);
        queryWrapper.orderBy(StringUtils.isNotBlank(sortField), sortOrder.equals(CommonConst.SORT_ORDER_ASC), sortField);
        Page<ApiInfo> apiInfoPage = apiInfoService.page(new Page<>(current, size), queryWrapper);
        return ResultUtils.success(apiInfoPage);
    }

    // endregion

    /**
     * 发布
     *
     * @param idRequest
     * @param request
     * @return
     */
    @PostMapping("/online")
    public BaseResponse<Boolean> onlineApiInfo(@RequestBody IdRequest idRequest, HttpServletRequest request) {
        // TODO: 2023/3/6 调用次数
        if (idRequest == null || idRequest.getId() <= 0) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_ERROR);
        }
        long id = idRequest.getId();
        // 判断是否存在
        ApiInfo oldApiInfo = apiInfoService.getById(id);
        if (oldApiInfo == null) {
            throw new ServiceException(ErrorCodeEnum.NOT_FOUND_ERROR);
        }
        // 判断该接口是否可以调用
        Test test = new Test();
        test.setTest("test");
        String response = testClient.post(test);
        if (StringUtils.isBlank(response)) {
            throw new ServiceException(ErrorCodeEnum.SYSTEM_ERROR, "接口验证失败");
        }
        // 仅本人或管理员可修改
        ApiInfo ApiInfo = new ApiInfo();
        ApiInfo.setId(id);
        ApiInfo.setStatus(1);
        boolean result = apiInfoService.updateById(ApiInfo);
        return ResultUtils.success(result);
    }

    /**
     * 下线
     *
     * @param idRequest
     * @param request
     * @return
     */
    @PostMapping("/offline")
    public BaseResponse<Boolean> offlineApiInfo(@RequestBody IdRequest idRequest, HttpServletRequest request) {
        if (idRequest == null || idRequest.getId() <= 0) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_ERROR);
        }
        long id = idRequest.getId();
        // 判断是否存在
        ApiInfo oldApiInfo = apiInfoService.getById(id);
        if (oldApiInfo == null) {
            throw new ServiceException(ErrorCodeEnum.NOT_FOUND_ERROR);
        }
        // 仅本人或管理员可修改
        ApiInfo ApiInfo = new ApiInfo();
        ApiInfo.setId(id);
        ApiInfo.setStatus(0);
        boolean result = apiInfoService.updateById(ApiInfo);
        return ResultUtils.success(result);
    }

    /**
     * 测试调用
     *
     * @param apiInfoInvokeRequest
     * @param request
     * @return
     */
    @PostMapping("/invoke")
    public BaseResponse<Object> invokeApiInfo(@RequestBody ApiInfoInvokeRequest apiInfoInvokeRequest, HttpServletRequest request) {
        if (apiInfoInvokeRequest == null || apiInfoInvokeRequest.getId() <= 0) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_ERROR);
        }
        long id = apiInfoInvokeRequest.getId();
        // 判断是否存在
        ApiInfo oldInterfaceInfo = apiInfoService.getById(id);
        if (oldInterfaceInfo == null) {
            throw new ServiceException(ErrorCodeEnum.NOT_FOUND_ERROR);
        }
        if (oldInterfaceInfo.getStatus() == 0) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_ERROR, "接口已关闭");
        }
        User loginUser = getLoginUser(request);
        String accessKey = loginUser.getAccessKey();
        String secretKey = loginUser.getSecretKey();
        TestClient tempClient = new TestClient(accessKey, secretKey);
        Gson gson = new Gson();
        Test test = gson.fromJson(apiInfoInvokeRequest.getRequestParams(), Test.class);
        String testResult = tempClient.post(test);
        return ResultUtils.success(testResult);
    }

    // /apiInfo
    @GetMapping("/getApiInfo")
    public BaseResponse<ApiInfo> getApiInfo(@RequestParam("url") String url, @RequestParam("method") String method) {
        return ResultUtils.success(apiInfoService.getApiInfo(url, method));
    }

    private User getLoginUser(HttpServletRequest request) {
        // 先判断是否已登录
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null || currentUser.getId() == null) {
            throw new ServiceException(ErrorCodeEnum.NOT_LOGIN_ERROR);
        }
        return currentUser;
    }

}
