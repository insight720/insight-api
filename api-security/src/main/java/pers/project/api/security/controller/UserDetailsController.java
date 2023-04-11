package pers.project.api.security.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.project.api.common.model.Result;
import pers.project.api.common.model.dto.LoginUserDTO;
import pers.project.api.common.util.ResultUtils;
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

}
