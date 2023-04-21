package pers.project.api.security.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pers.project.api.common.model.Result;
import pers.project.api.common.util.ResultUtils;
import pers.project.api.security.model.dto.UserAccountAuthorityDTO;
import pers.project.api.security.model.dto.UserAccountStatusDTO;
import pers.project.api.security.model.dto.UserRegistryDTO;
import pers.project.api.security.model.vo.ApiKeyPairVO;
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
        userAccountService.saveNewAccount(userRegistryDTO);
        return ResultUtils.success();
    }

    /**
     * @see <a href="https://github.com/spring-projects/spring-security/issues/13058">ISSUES</a>
     */
    @GetMapping("/{accountId}/key")
    public Result<ApiKeyPairVO> getNewApiKeyPair(@NotBlank @PathVariable String accountId) {
        ApiKeyPairVO apiKeyPairVO = userAccountService.getApiKeyPairVO(accountId);
        return ResultUtils.success(apiKeyPairVO);
    }

    @PutMapping("/status")
    public Result<Void> modifyAccountStatus(@Valid @RequestBody UserAccountStatusDTO accountStatusDTO) {
        userAccountService.updateAccountStatus(accountStatusDTO);
        return ResultUtils.success();
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("/authority")
    public Result<Void> modifyAccountAuthority(@Valid @RequestBody UserAccountAuthorityDTO accountAuthorityDTO) {
        userAccountService.updateAccountAuthority(accountAuthorityDTO);
        return ResultUtils.success();
    }

}
