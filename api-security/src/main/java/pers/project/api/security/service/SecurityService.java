package pers.project.api.security.service;

import jakarta.servlet.http.HttpServletRequest;
import pers.project.api.security.model.CustomCsrfToken;

/**
 * Security 模块 Service
 *
 * @author Luo Fei
 * @date 2023/03/24
 */
public interface SecurityService {

    /**
     * 获取 CSRF 令牌
     *
     * @param request HTTP 请求
     */
    CustomCsrfToken getCsrfToken(HttpServletRequest request);


}
