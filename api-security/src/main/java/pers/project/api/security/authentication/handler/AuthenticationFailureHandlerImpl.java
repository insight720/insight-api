package pers.project.api.security.authentication.handler;

import com.alibaba.fastjson2.JSON;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import pers.project.api.common.model.Result;
import pers.project.api.security.enumeration.VerificationStrategyEnum;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_USERNAME_KEY;
import static pers.project.api.common.enumeration.ErrorEnum.LOGIN_ERROR;
import static pers.project.api.common.util.ResultUtils.failure;
import static pers.project.api.security.authentication.VerificationCodeAuthenticationFilter.SPRING_SECURITY_FORM_STRATEGY_KEY;

/**
 * Spring Security 认证失败处理程序
 *
 * @author Luo Fei
 * @date 2023/03/21
 * @see AuthenticationFailureHandler
 */
@Slf4j
@Component
public class AuthenticationFailureHandlerImpl implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(JSON.toJSONString(determineResult(request, exception)));
    }

    private static Result<Void> determineResult(HttpServletRequest request, AuthenticationException exception) {
        if (exception instanceof BadCredentialsException) {
            String username = request.getParameter(SPRING_SECURITY_FORM_USERNAME_KEY);
            if (StringUtils.isNotBlank(username)) {
                return failure(LOGIN_ERROR, "用户名或密码错误");
            }
            String strategy = request.getParameter(SPRING_SECURITY_FORM_STRATEGY_KEY);
            VerificationStrategyEnum strategyEnum = EnumUtils.getEnum(VerificationStrategyEnum.class, strategy);
            Assert.notNull(strategyEnum, "Since the username does not exist, the strategy must not be null");
            return switch (strategyEnum) {
                case PHONE -> failure(LOGIN_ERROR, "手机号或验证码错误");
                case EMAIL -> failure(LOGIN_ERROR, "邮箱或验证码错误");
            };
        }
        if (exception instanceof AuthenticationServiceException) {
            log.warn("An authentication request could not be processed due to a system problem", exception);
            return failure(LOGIN_ERROR, "系统错误，登陆失败");
        }
        throw new IllegalStateException("The attempt to determine the login failure response result fails");
    }

}