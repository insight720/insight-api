package pers.project.api.security.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pers.project.api.common.model.Result;
import pers.project.api.common.util.ResultUtils;
import pers.project.api.security.model.dto.*;
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
    public Result<Void> register(@Valid @RequestBody UserAccountRegistryDTO userAccountRegistryDTO) {
        userAccountService.createNewAccount(userAccountRegistryDTO);
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
    public Result<Void> modifyApiKeyStatus(@Valid @RequestBody ApiKeyStatusModificationDTO modificationDTO) {
        userAccountService.updateApiKeyStatus(modificationDTO);
        return ResultUtils.success();
    }

    @PutMapping("/non/admin/authority")
    public Result<Void> modifyNonAdminAuthority(@Valid @RequestBody NonAdminAuthorityModificationDTO modificationDTO) {
        userAccountService.updateNonAdminAuthority(modificationDTO);
        return ResultUtils.success();
    }

    @PutMapping("/username")
    public Result<Void> modifyUsername(@Valid @RequestBody UsernameModificationDTO modificationDTO) {
        userAccountService.updateUsername(modificationDTO);
        return ResultUtils.success();
    }

    @PutMapping("/username/and/password/setting")
    public Result<Void> setUsernameAndPassword(@Valid @RequestBody UsernameAndPasswordSettingDTO settingDTO) {
        userAccountService.updateUsernameAndPassword(settingDTO);
        return ResultUtils.success();
    }

    @PutMapping("/password")
    public Result<Void> modifyPassword(@Valid @RequestBody PasswordModificationDTO modificationDTO) {
        userAccountService.updatePassword(modificationDTO);
        return ResultUtils.success();
    }

    @DeleteMapping("/deletion")
    public Result<Void> deleteAccount(@Valid @RequestBody AccountVerificationCodeCheckDTO codeCheckDTO) {
        userAccountService.removeAccount(codeCheckDTO);
        return ResultUtils.success();
    }

    @PutMapping("/phone/or/email/binding")
    public Result<Void> bindPhoneOrEmail(@Valid @RequestBody AccountVerificationCodeCheckDTO codeCheckDTO) {
        userAccountService.savePhoneNumberOrEmailAddress(codeCheckDTO);
        return ResultUtils.success();
    }

    @DeleteMapping("/phone/or/email/unbinding")
    public Result<Void> unbindPhoneOrEmail(@Valid @RequestBody AccountVerificationCodeCheckDTO codeCheckDTO) {
        userAccountService.removePhoneNumberOrEmailAddress(codeCheckDTO);
        return ResultUtils.success();
    }

}
