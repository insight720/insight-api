package pers.project.api.security.web.access;

import com.alibaba.fastjson2.JSON;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Component;
import pers.project.api.common.util.ResultUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static pers.project.api.common.constant.enumeration.ErrorEnum.AUTHORIZATION_ERROR;

/**
 * Spring Security 授权异常处理程序
 * <p>
 * 如果用户已认证但未被授权，过滤器将应用程序抛出的
 * AccessDeniedException 委托给此类处理。
 *
 * @author Luo Fei
 * @date 2023/03/21
 * @see AccessDeniedHandler
 */
@Component
public class AccessDeniedHandlerImpl implements AccessDeniedHandler {


    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        System.out.println("accessDeniedException.getMessage() = " + accessDeniedException.getMessage());
        System.out.println("request.getAttribute(\"_csrf\") = " + ((CsrfToken) request.getAttribute("_csrf")).getToken());
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(JSON.toJSONString(ResultUtils.failure(AUTHORIZATION_ERROR)));
    }

}
