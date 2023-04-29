package pers.project.api.security.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pers.project.api.common.model.Result;
import pers.project.api.common.util.ResultUtils;
import pers.project.api.security.model.dto.AccountVerificationCodeCheckDTO;
import pers.project.api.security.model.dto.ApiKeyStatusDTO;
import pers.project.api.security.model.dto.NonAdminAuthorityDTO;
import pers.project.api.security.model.dto.UserRegistryDTO;
import pers.project.api.security.service.UserAccountService;

/**
 * 用户帐户控制器
 *
 * @author Luo Fei
 * @date 2023/03/22
 */
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/account")
public class UserAccountController {

    private final UserAccountService userAccountService;

    @PostMapping("/registry")
    public Result<Void> register(@Valid @RequestBody UserRegistryDTO userRegistryDTO) {
        userAccountService.createNewAccount(userRegistryDTO);
        return ResultUtils.success();
    }

    @PostMapping("/new/api/key")
    public Result<String> getNewApiKey(@Valid @RequestBody AccountVerificationCodeCheckDTO codeCheckDTO) {
        String secretKey = userAccountService.generateApiKey(codeCheckDTO);
        return ResultUtils.success(secretKey);
    }

    @PostMapping("/secret/key")
    public Result<String> viewSecretKey(@Valid @RequestBody AccountVerificationCodeCheckDTO codeCheckDTO) {
        String secretKey = userAccountService.getSecretKey(codeCheckDTO);
        return ResultUtils.success(secretKey);
    }

    @PutMapping("/api/key/status")
    public Result<Void> modifyApiKeyStatus(@Valid @RequestBody ApiKeyStatusDTO apiKeyStatusDTO) {
        userAccountService.updateApiKeyStatus(apiKeyStatusDTO);
        return ResultUtils.success();
    }

    @PutMapping("/non/admin/authority")
    public Result<Void> modifyNonAdminAuthority(@Valid @RequestBody NonAdminAuthorityDTO authorityDTO) {
        userAccountService.updateNonAdminAuthority(authorityDTO);
        return ResultUtils.success();
    }

}
