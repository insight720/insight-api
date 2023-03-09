package pers.project.api.security.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.Gson;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pers.project.api.sdk.client.TestClient;
import pers.project.api.sdk.model.Test;
import pers.project.api.security.annotation.AuthCheck;
import pers.project.api.security.common.*;
import pers.project.api.security.constant.CommonConstant;
import pers.project.api.security.exception.BusinessException;
import pers.project.api.security.model.dto.api_info.ApiInfoAddRequest;
import pers.project.api.security.model.dto.api_info.ApiInfoInvokeRequest;
import pers.project.api.security.model.dto.api_info.ApiInfoQueryRequest;
import pers.project.api.security.model.dto.api_info.ApiInfoUpdateRequest;
import pers.project.api.security.model.entity.ApiInfo;
import pers.project.api.common.model.entity.User;
import pers.project.api.security.service.ApiInfoService;
import pers.project.api.security.service.UserService;

import java.util.List;

/**
 * 帖子接口
 *
 * @author yupi
 */
@RestController
@RequestMapping("/apiInfo")
@Slf4j
public class ApiInfoController {

    @Resource
    private ApiInfoService apiInfoService;

    @Resource
    private UserService userService;

    @Autowired
    private TestClient testClient;


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
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        ApiInfo apiInfo = new ApiInfo();
        BeanUtils.copyProperties(apiInfoAddRequest, apiInfo);
        // 校验
        apiInfoService.validApiInfo(apiInfo, true);
        User loginUser = userService.getLoginUser(request);
        apiInfo.setUserId(loginUser.getId());
        boolean result = apiInfoService.save(apiInfo);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
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
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        ApiInfo oldApiInfo = apiInfoService.getById(id);
        if (oldApiInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 仅本人或管理员可删除
        if (!oldApiInfo.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
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
    public BaseResponse<Boolean> updateApiInfo(@RequestBody ApiInfoUpdateRequest apiInfoUpdateRequest,
                                               HttpServletRequest request) {
        if (apiInfoUpdateRequest == null || apiInfoUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        ApiInfo apiInfo = new ApiInfo();
        BeanUtils.copyProperties(apiInfoUpdateRequest, apiInfo);
        // 参数校验
        apiInfoService.validApiInfo(apiInfo, false);
        User user = userService.getLoginUser(request);
        long id = apiInfoUpdateRequest.getId();
        // 判断是否存在
        ApiInfo oldApiInfo = apiInfoService.getById(id);
        if (oldApiInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 仅本人或管理员可修改
        if (!oldApiInfo.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
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
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
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
    @AuthCheck(mustRole = "admin")
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
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
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
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<ApiInfo> queryWrapper = new QueryWrapper<>(apiInfoQuery);
        queryWrapper.like(StringUtils.isNotBlank(description), "description", description);
        queryWrapper.orderBy(StringUtils.isNotBlank(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC), sortField);
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
    @AuthCheck(mustRole = "admin")
    public BaseResponse<Boolean> onlineApiInfo(@RequestBody IdRequest idRequest,
                                               HttpServletRequest request) {
        // TODO: 2023/3/6 调用次数
        if (idRequest == null || idRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long id = idRequest.getId();
        // 判断是否存在
        ApiInfo oldApiInfo = apiInfoService.getById(id);
        if (oldApiInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 判断该接口是否可以调用
        Test test = new Test();
        test.setTest("test");
        String response = testClient.post(test);
        if (StringUtils.isBlank(response)) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "接口验证失败");
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
    @AuthCheck(mustRole = "admin")
    public BaseResponse<Boolean> offlineApiInfo(@RequestBody IdRequest idRequest,
                                                HttpServletRequest request) {
        if (idRequest == null || idRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long id = idRequest.getId();
        // 判断是否存在
        ApiInfo oldApiInfo = apiInfoService.getById(id);
        if (oldApiInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
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
    public BaseResponse<Object> invokeApiInfo(@RequestBody ApiInfoInvokeRequest apiInfoInvokeRequest,
                                              HttpServletRequest request) {
        if (apiInfoInvokeRequest == null || apiInfoInvokeRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long id = apiInfoInvokeRequest.getId();
        // 判断是否存在
        ApiInfo oldInterfaceInfo = apiInfoService.getById(id);
        if (oldInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        if (oldInterfaceInfo.getStatus() == 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口已关闭");
        }
        User loginUser = userService.getLoginUser(request);
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
    public BaseResponse<ApiInfo> getApiInfo(@RequestParam("url") String url,
                                            @RequestParam("method") String method) {
        return ResultUtils.success(apiInfoService.getApiInfo(url, method));
    }

}
