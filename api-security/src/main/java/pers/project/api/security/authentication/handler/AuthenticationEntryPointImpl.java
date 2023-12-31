package pers.project.api.security.authentication.handler;

import com.alibaba.fastjson2.JSON;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import pers.project.api.common.model.Result;
import pers.project.api.common.util.ResultUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static pers.project.api.common.enumeration.ErrorEnum.LOGIN_ERROR;

/**
 * Spring Security 身份验证入口点
 * <p>
 * 如果用户没有被认证，或者应用程序抛出 AuthenticationException，那么进入
 * 此类启动身份验证方案。
 *
 * @author Luo Fei
 * @date 2023/03/21
 * @see <a href="https://springdoc.cn/spring-security/servlet/authentication/architecture.html#servlet-authentication-authenticationentrypoint">
 * 用 AuthenticationEntryPoint 请求凭证</a>
 */
@Component
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        Result<Object> result = ResultUtils.failure(LOGIN_ERROR, "用户未登录");
        response.getWriter().write(JSON.toJSONString(result));
    }

}
