package pers.project.api.security.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pers.project.api.common.model.Result;
import pers.project.api.common.util.ResultUtils;
import pers.project.api.security.model.dto.AccountAuthorityDTO;
import pers.project.api.security.model.dto.AccountStatusDTO;
import pers.project.api.security.model.dto.KeyPairDTO;
import pers.project.api.security.model.vo.UserRegistryVO;
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

    @GetMapping("/key/{accountId}")
    public Result<KeyPairDTO> generateKeyPair(@NotBlank @PathVariable String accountId) {
        //  TODO: 2023/4/6 1 需不需要返回 2 更不更新信息
        KeyPairDTO keyPairDTO = userAccountService.getKeyPairDTO(accountId);
        return ResultUtils.success(keyPairDTO);
    }

    @PostMapping("/registry")
    public Result<Void> registerUser(@Valid @RequestBody UserRegistryVO userRegistryVO) {
        userAccountService.saveAccount(userRegistryVO);
        return ResultUtils.success();
    }

    @PutMapping("/status")
    public Result<Void> modifyAccountStatus(@Valid @RequestBody AccountStatusDTO accountStatusDTO) {
        userAccountService.updateAccountStatus(accountStatusDTO);
        return ResultUtils.success();
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("/authority")
    public Result<Void> modifyAccountAuthority(@Valid @RequestBody AccountAuthorityDTO accountAuthorityDTO) {
        userAccountService.updateAccountAuthority(accountAuthorityDTO);
        return ResultUtils.success();
    }

}
