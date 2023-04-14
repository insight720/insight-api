package pers.project.api.security.web.authentication;

import com.alibaba.fastjson2.JSON;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import pers.project.api.common.util.ResultUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static pers.project.api.common.enumeration.ErrorEnum.AUTHENTICATION_ERROR;

/**
 * Spring Security 认证失败处理程序
 *
 * @author Luo Fei
 * @date 2023/03/21
 * @see AuthenticationFailureHandler
 */
@Component
public class AuthenticationFailureHandlerImpl implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        if (exception instanceof DisabledException disabledException) {
            // 用户被禁用
        } else if (exception instanceof LockedException lockedException) {
            // 用户无可用权限角色
        } else if (exception instanceof AuthenticationServiceException serviceException) {
            // BadCredentialsException
            // 用户名或密码错误
        }
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(JSON.toJSONString(ResultUtils.failure(AUTHENTICATION_ERROR)));
    }

}