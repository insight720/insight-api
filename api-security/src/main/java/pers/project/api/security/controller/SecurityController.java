package pers.project.api.security.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.project.api.common.model.Result;
import pers.project.api.common.util.ResultUtils;
import pers.project.api.security.model.CustomCsrfToken;
import pers.project.api.security.service.SecurityService;

/**
 * Security 模块控制器
 *
 * @author Luo Fei
 * @date 2023/03/24
 */
@Validated
@RestController
@RequiredArgsConstructor
public class SecurityController {

    private final SecurityService securityService;

    @GetMapping("/csrf")
    public Result<CustomCsrfToken> autoGetCsrfToken(HttpServletRequest request) {
        CustomCsrfToken csrfToken = securityService.getCsrfToken(request);
        return ResultUtils.success(csrfToken);
    }

}
