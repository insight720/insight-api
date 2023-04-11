package pers.project.api.security.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Service;
import pers.project.api.security.service.SecurityService;

/**
 * Security 模块的 Service 实现
 *
 * @author Luo Fei
 * @date 2023/03/24
 */
@Slf4j
@Service
public class SecurityServiceImpl implements SecurityService {

    private static final String CSRF_TOKEN_ATTRIBUTE = "_csrf";

    @Override
    public void loadDeferredCsrfToken(HttpServletRequest request) {
        Object attribute = request.getAttribute(CSRF_TOKEN_ATTRIBUTE);
        // instanceof 可以判空
        if (attribute instanceof CsrfToken csrfToken) {
            // 必须调用，否则 CsrfToken 被 Lambda 表达式延迟加载，不会生成 Cookie
            csrfToken.getToken();
            return;
        }
        throw new IllegalStateException("Should never get here");
    }

}
