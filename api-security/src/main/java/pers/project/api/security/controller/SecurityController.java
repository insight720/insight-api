package pers.project.api.security.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pers.project.api.common.model.Result;
import pers.project.api.common.model.dto.ClientUserInfoDTO;
import pers.project.api.common.model.query.*;
import pers.project.api.common.model.vo.ApiAdminPageVO;
import pers.project.api.common.model.vo.UserAdminPageVO;
import pers.project.api.common.model.vo.UserApiDigestPageVO;
import pers.project.api.common.model.vo.UserApiFormatAndQuantityUsageVO;
import pers.project.api.common.util.ResultUtils;
import pers.project.api.common.validation.constraint.SnowflakeId;
import pers.project.api.security.model.dto.UserApiTestDTO;
import pers.project.api.security.model.dto.VerificationCodeSendingDTO;
import pers.project.api.security.model.vo.ApiCreatorVO;
import pers.project.api.security.model.vo.UserApiTestVO;
import pers.project.api.security.service.SecurityService;

/**
 * Security 项目控制器
 *
 * @author Luo Fei
 * @date 2023/03/24
 */
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
public class SecurityController {

    private final SecurityService securityService;

    // region OpenFeign
    @PostMapping("/request/user/info/result")
    public Result<ClientUserInfoDTO> getClientUserInfoResult(@Valid @RequestBody ClientUserInfoQuery userInfoQuery) {
        ClientUserInfoDTO clientUserInfoDTO = securityService.getClientUserInfoDTO(userInfoQuery);
        return ResultUtils.success(clientUserInfoDTO);
    }
    // endregion

    @GetMapping("/csrf/token")
    public Result<Void> getCsrfToken(HttpServletRequest request) {
        securityService.loadDeferredCsrfToken(request);
        return ResultUtils.success();
    }

    @PostMapping("/verification/code")
    public Result<Void> getVerificationCode(@Valid @RequestBody VerificationCodeSendingDTO codeSendingDTO) {
        securityService.sendVerificationCode(codeSendingDTO);
        return ResultUtils.success();
    }

    @PostMapping("/user/api/digest/page")
    public Result<UserApiDigestPageVO> viewUserApiDigestPage
            (@Valid @RequestBody UserApiDigestPageQuery pageQuery) {
        UserApiDigestPageVO pageVO = securityService.getUserApiDigestPageVO(pageQuery);
        return ResultUtils.success(pageVO);
    }

    @PostMapping("/user/api/format/and/quantity/usage")
    public Result<UserApiFormatAndQuantityUsageVO> viewUserApiFormatAndQuantityUsage
            (@Valid @RequestBody UserApiFormatAndQuantityUsageQuery query) {
        UserApiFormatAndQuantityUsageVO formatAndQuantityUsageVO
                = securityService.getUserApiFormatAndQuantityUsageVO(query);
        return ResultUtils.success(formatAndQuantityUsageVO);
    }

    @PostMapping("/user/admin/page")
    public Result<UserAdminPageVO> viewUserAdminPage
            (@Valid @RequestBody UserAdminPageQuery pageQuery) {
        UserAdminPageVO pageVO = securityService.getUserAdminPageVO(pageQuery);
        return ResultUtils.success(pageVO);
    }

    @PostMapping("/api/admin/page")
    public Result<ApiAdminPageVO> viewApiAdminPage
            (@Valid @RequestBody ApiAdminPageQuery pageQuery) {
        ApiAdminPageVO pageVO = securityService.getApiAdminPageVO(pageQuery);
        return ResultUtils.success(pageVO);
    }

    @PostMapping("/api/creator/{accountId}")
    public Result<ApiCreatorVO> viewApiCreator
            (@SnowflakeId @PathVariable String accountId) {
        ApiCreatorVO apiCreatorVO = securityService.getApiCreatorVO(accountId);
        return ResultUtils.success(apiCreatorVO);
    }

    @PostMapping("/user/api/test")
    public Result<UserApiTestVO> testUserApi
            (@Valid @RequestBody UserApiTestDTO UserApiTestDTO) {
        UserApiTestVO userApiTestVO = securityService.getUserApiTestVO(UserApiTestDTO);
        return ResultUtils.success(userApiTestVO);
    }

}
