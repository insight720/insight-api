package pers.project.api.security.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.project.api.common.model.Result;
import pers.project.api.common.model.dto.LoginUserDTO;
import pers.project.api.common.util.ResultUtils;
import pers.project.api.security.service.CustomUserDetailsService;

/**
 * Spring Security用户详细信息控制器
 *
 * @author Luo Fei
 * @date 2023/03/24
 */
@Validated
@RestController
@RequiredArgsConstructor
public class UserDetailsController {

    private final CustomUserDetailsService customUserDetailsService;

    @GetMapping("/details/current")
    public Result<LoginUserDTO> fetchLoginUserInfo(HttpServletRequest request) {
        LoginUserDTO loginUserDTO = customUserDetailsService.getLoginUserDetails(request);
        return ResultUtils.success(loginUserDTO);
    }

}
