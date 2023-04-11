package pers.project.api.security.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;

import java.util.function.Supplier;

/**
 * Security 模块 Service
 *
 * @author Luo Fei
 * @date 2023/03/24
 */
public interface SecurityService {

    /**
     * 加载被延迟生成的 CSRF 令牌
     *
     * @param request HTTP 请求
     * @see XorCsrfTokenRequestAttributeHandler#handle(HttpServletRequest, HttpServletResponse, Supplier)
     */
    void loadDeferredCsrfToken(HttpServletRequest request);

}
