package pers.project.api.security.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pers.project.api.common.model.Result;
import pers.project.api.common.model.dto.LoginUserDTO;
import pers.project.api.common.util.ResultUtils;
import pers.project.api.security.model.dto.PhoneOrEmailLoginDTO;
import pers.project.api.security.service.CustomUserDetailsService;

/**
 * Spring Security 用户详细信息控制器
 *
 * @author Luo Fei
 * @date 2023/03/24
 */
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/details")
public class UserDetailsController {

    private final CustomUserDetailsService customUserDetailsService;

    @GetMapping("/user")
    public Result<LoginUserDTO> getLoginUserInfo() {
        LoginUserDTO loginUserDTO = customUserDetailsService.getLoginUserDTO();
        return ResultUtils.success(loginUserDTO);
    }

    @PostMapping("/login")
    public Result<LoginUserDTO> loginByVerificationCode(@Valid @RequestBody PhoneOrEmailLoginDTO loginDTO) {
        LoginUserDTO loginUserDTO = customUserDetailsService.loginByPhoneOrEmail(loginDTO);
        return ResultUtils.success(loginUserDTO);
    }

}
