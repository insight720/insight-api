package pers.project.api.security.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pers.project.api.common.model.Result;
import pers.project.api.common.util.ResultUtils;
import pers.project.api.security.model.dto.VerificationCodeSendingDTO;
import pers.project.api.security.service.SecurityService;

/**
 * Security 控制器
 *
 * @author Luo Fei
 * @date 2023/03/24
 */
@Validated
@RestController
@RequiredArgsConstructor
public class SecurityController {

    private final SecurityService securityService;

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

}
