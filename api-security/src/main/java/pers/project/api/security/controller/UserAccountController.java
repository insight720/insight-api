package pers.project.api.security.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.project.api.common.model.Result;
import pers.project.api.common.util.ResultUtils;
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


    @PostMapping("/registry")
    public Result<Void> registerUser(@Valid @RequestBody UserRegistryVO userRegistryVO) {
        userAccountService.saveAccount(userRegistryVO);
        return ResultUtils.success();
    }

}
