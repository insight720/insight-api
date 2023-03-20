package pers.project.api.facade.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.nacos.common.utils.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import pers.project.api.common.constant.CommonConst;
import pers.project.api.common.constant.enumeration.ErrorEnum;
import pers.project.api.common.exception.ServiceException;
import pers.project.api.common.model.Response;
import pers.project.api.common.model.entity.ApiInfoEntity;
import pers.project.api.common.model.entity.UserEntity;
import pers.project.api.common.model.request.DeleteRequest;
import pers.project.api.common.model.request.IdRequest;
import pers.project.api.common.util.ResponseUtils;
import pers.project.api.facade.feign.ProviderFeignService;
import pers.project.api.facade.model.request.apiinfo.ApiInfoAddRequest;
import pers.project.api.facade.model.request.apiinfo.ApiInfoInvokeRequest;
import pers.project.api.facade.model.request.apiinfo.ApiInfoQueryRequest;
import pers.project.api.facade.model.request.apiinfo.ApiInfoUpdateRequest;
import pers.project.api.facade.service.ApiInfoService;
import pers.project.api.sdk.client.TestClient;
import pers.project.api.sdk.model.Test;

import java.util.List;

import static pers.project.api.common.constant.UserConst.USER_LOGIN_STATE;


/**
 * 接口信息控制器
 *
 * @author Luo Fei
 * @date 2023/03/09
 */
@RestController
public class ApiInfoController {

    @Resource
    private ApiInfoService apiInfoService;

    @Resource
    private TestClient testClient;

    @Resource
    private ProviderFeignService providerFeignService;

    // TODO: 2023/03/9 权限校验
    /**
     * 测试调用
     *
     * @param apiInfoInvokeRequest
     * @param request
     * @return
     */
    @PostMapping("/invoke")
    public Response<Object> invokeApiInfo(@RequestBody ApiInfoInvokeRequest apiInfoInvokeRequest, HttpServletRequest request) {
        if (apiInfoInvokeRequest == null || apiInfoInvokeRequest.getId() <= 0) {
            throw new ServiceException(ErrorEnum.PARAMS_ERROR);
        }
        long id = apiInfoInvokeRequest.getId();
        // 判断是否存在
        ApiInfoEntity oldInterfaceInfo = apiInfoService.getById(id);
        if (oldInterfaceInfo == null) {
            throw new ServiceException(ErrorEnum.NOT_FOUND_ERROR);
        }
        if (oldInterfaceInfo.getStatus() == 0) {
            throw new ServiceException(ErrorEnum.PARAMS_ERROR, "接口已关闭");
        }
        UserEntity loginUserEntity = getLoginUser(request);
        String accessKey = loginUserEntity.getAccessKey();
        String secretKey = loginUserEntity.getSecretKey();
        TestClient tempClient = new TestClient(accessKey, secretKey);
        String requestParams = apiInfoInvokeRequest.getRequestParams();
        String testResult;
        Cookie[] cookies = request.getCookies();
        if (!requestParams.equals("get")) {
            Test test = JSON.parseObject(requestParams, Test.class);
            testResult = tempClient.post(test, cookies[0].getName(), cookies[0].getValue());
        } else {
            HttpHeaders httpHeaders = tempClient.getHeaders(requestParams);
            testResult = providerFeignService.get("test", httpHeaders, cookies[0]);
        }
        return ResponseUtils.success(testResult);
    }

    // region 增删改查

    /**
     * 创建
     *
     * @param apiInfoAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public Response<Long> addApiInfo(@RequestBody ApiInfoAddRequest apiInfoAddRequest, HttpServletRequest request) {
        if (apiInfoAddRequest == null) {
            throw new ServiceException(ErrorEnum.PARAMS_ERROR);
        }
        ApiInfoEntity apiInfoEntity = new ApiInfoEntity();
        BeanUtils.copyProperties(apiInfoAddRequest, apiInfoEntity);
        // 校验
        apiInfoService.validApiInfo(apiInfoEntity, true);
        UserEntity loginUserEntity = getLoginUser(request);
        apiInfoEntity.setUserId(loginUserEntity.getId());
        boolean result = apiInfoService.save(apiInfoEntity);
        if (!result) {
            throw new ServiceException(ErrorEnum.OPERATION_ERROR);
        }
        long newApiInfoId = apiInfoEntity.getId();
        return ResponseUtils.success(newApiInfoId);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public Response<Boolean> deleteApiInfo(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new ServiceException(ErrorEnum.PARAMS_ERROR);
        }
        UserEntity userEntity = getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        ApiInfoEntity oldApiInfoEntity = apiInfoService.getById(id);
        if (oldApiInfoEntity == null) {
            throw new ServiceException(ErrorEnum.NOT_FOUND_ERROR);
        }
        // 仅本人或管理员可删除
        if (!oldApiInfoEntity.getUserId().equals(userEntity.getId())) {
            throw new ServiceException(ErrorEnum.NO_AUTH_ERROR);
        }
        boolean b = apiInfoService.removeById(id);
        return ResponseUtils.success(b);
    }

    /**
     * 更新
     *
     * @param apiInfoupdateRequest
     * @param request
     * @return
     */
    @PostMapping("/update")
    public Response<Boolean> updateApiInfo(@RequestBody ApiInfoUpdateRequest apiInfoupdateRequest, HttpServletRequest request) {
        if (apiInfoupdateRequest == null || apiInfoupdateRequest.getId() <= 0) {
            throw new ServiceException(ErrorEnum.PARAMS_ERROR);
        }
        ApiInfoEntity apiInfoEntity = new ApiInfoEntity();
        BeanUtils.copyProperties(apiInfoupdateRequest, apiInfoEntity);
        // 参数校验
        apiInfoService.validApiInfo(apiInfoEntity, false);
        UserEntity userEntity = getLoginUser(request);
        long id = apiInfoupdateRequest.getId();
        // 判断是否存在
        ApiInfoEntity oldApiInfoEntity = apiInfoService.getById(id);
        if (oldApiInfoEntity == null) {
            throw new ServiceException(ErrorEnum.NOT_FOUND_ERROR);
        }
        // 仅本人或管理员可修改
        if (!oldApiInfoEntity.getUserId().equals(userEntity.getId())) {
            throw new ServiceException(ErrorEnum.NO_AUTH_ERROR);
        }
        boolean result = apiInfoService.updateById(apiInfoEntity);
        return ResponseUtils.success(result);
    }

    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    public Response<ApiInfoEntity> getApiInfoById(long id) {
        if (id <= 0) {
            throw new ServiceException(ErrorEnum.PARAMS_ERROR);
        }
        ApiInfoEntity apiInfoEntity = apiInfoService.getById(id);
        return ResponseUtils.success(apiInfoEntity);
    }

    /**
     * 获取列表（仅管理员可使用）
     *
     * @param apiInfoQueryRequest
     * @return
     */
    @GetMapping("/list")
    public Response<List<ApiInfoEntity>> listApiInfo(ApiInfoQueryRequest apiInfoQueryRequest) {
        ApiInfoEntity apiInfoEntityQuery = new ApiInfoEntity();
        if (apiInfoQueryRequest != null) {
            BeanUtils.copyProperties(apiInfoQueryRequest, apiInfoEntityQuery);
        }
        QueryWrapper<ApiInfoEntity> queryWrapper = new QueryWrapper<>(apiInfoEntityQuery);
        List<ApiInfoEntity> apiInfoEntityList = apiInfoService.list(queryWrapper);
        return ResponseUtils.success(apiInfoEntityList);
    }

    /**
     * 分页获取列表
     *
     * @param apiInfoQueryRequest
     * @param request
     * @return
     */
    @GetMapping("/list/page")
    public Response<Page<ApiInfoEntity>> listApiInfoByPage(ApiInfoQueryRequest apiInfoQueryRequest, HttpServletRequest request) {
        if (apiInfoQueryRequest == null) {
            throw new ServiceException(ErrorEnum.PARAMS_ERROR);
        }
        ApiInfoEntity apiInfoEntityQuery = new ApiInfoEntity();
        BeanUtils.copyProperties(apiInfoQueryRequest, apiInfoEntityQuery);
        long current = apiInfoQueryRequest.getCurrent();
        long size = apiInfoQueryRequest.getPageSize();
        String sortField = apiInfoQueryRequest.getSortField();
        String sortOrder = apiInfoQueryRequest.getSortOrder();
        String description = apiInfoEntityQuery.getDescription();
        // description 需支持模糊搜索
        apiInfoEntityQuery.setDescription(null);
        // 限制爬虫
        if (size > 50) {
            throw new ServiceException(ErrorEnum.PARAMS_ERROR);
        }
        QueryWrapper<ApiInfoEntity> queryWrapper = new QueryWrapper<>(apiInfoEntityQuery);
        queryWrapper.like(StringUtils.isNotBlank(description), "description", description);
        queryWrapper.orderBy(StringUtils.isNotBlank(sortField), sortOrder.equals(CommonConst.SORT_ORDER_ASC), sortField);
        Page<ApiInfoEntity> apiInfoPage = apiInfoService.page(new Page<>(current, size), queryWrapper);
        return ResponseUtils.success(apiInfoPage);
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
    public Response<Boolean> onlineApiInfo(@RequestBody IdRequest idRequest, HttpServletRequest request) {
        // TODO: 2023/03/6 调用次数
        if (idRequest == null || idRequest.getId() <= 0) {
            throw new ServiceException(ErrorEnum.PARAMS_ERROR);
        }
        long id = idRequest.getId();
        // 判断是否存在
        ApiInfoEntity oldApiInfoEntity = apiInfoService.getById(id);
        if (oldApiInfoEntity == null) {
            throw new ServiceException(ErrorEnum.NOT_FOUND_ERROR);
        }
        // 判断该接口是否可以调用
        Test test = new Test();
        test.setTest("test");
        Cookie[] cookies = request.getCookies();
        Cookie session = cookies[0];
        String response = testClient.post(test, session.getName(), session.getValue());
        if (StringUtils.isBlank(response)) {
            throw new ServiceException(ErrorEnum.SYSTEM_ERROR, "接口验证失败");
        }
        // 仅本人或管理员可修改
        ApiInfoEntity ApiInfoEntity = new ApiInfoEntity();
        ApiInfoEntity.setId(id);
        ApiInfoEntity.setStatus(1);
        boolean result = apiInfoService.updateById(ApiInfoEntity);
        return ResponseUtils.success(result);
    }

    /**
     * 下线
     *
     * @param idRequest
     * @param request
     * @return
     */
    @PostMapping("/offline")
    public Response<Boolean> offlineApiInfo(@RequestBody IdRequest idRequest, HttpServletRequest request) {
        if (idRequest == null || idRequest.getId() <= 0) {
            throw new ServiceException(ErrorEnum.PARAMS_ERROR);
        }
        long id = idRequest.getId();
        // 判断是否存在
        ApiInfoEntity oldApiInfoEntity = apiInfoService.getById(id);
        if (oldApiInfoEntity == null) {
            throw new ServiceException(ErrorEnum.NOT_FOUND_ERROR);
        }
        // 仅本人或管理员可修改
        ApiInfoEntity ApiInfoEntity = new ApiInfoEntity();
        ApiInfoEntity.setId(id);
        ApiInfoEntity.setStatus(0);
        boolean result = apiInfoService.updateById(ApiInfoEntity);
        return ResponseUtils.success(result);
    }



    // /apiInfo
    @GetMapping("/getApiInfo")
    public Response<ApiInfoEntity> getApiInfo(@RequestParam("url") String url, @RequestParam("method") String method) {
        return ResponseUtils.success(apiInfoService.getApiInfo(url, method));
    }

    private UserEntity getLoginUser(HttpServletRequest request) {
        // 先判断是否已登录
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        UserEntity currentUserEntity = (UserEntity) userObj;
        if (currentUserEntity == null || currentUserEntity.getId() == null) {
            throw new ServiceException(ErrorEnum.NOT_LOGIN_ERROR);
        }
        return currentUserEntity;
    }

}
