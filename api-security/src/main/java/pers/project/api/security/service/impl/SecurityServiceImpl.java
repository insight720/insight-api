package pers.project.api.security.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Service;
import pers.project.api.common.exception.ServerException;
import pers.project.api.security.model.CustomCsrfToken;
import pers.project.api.security.service.SecurityService;

import static pers.project.api.common.constant.enumeration.ErrorEnum.SERVER_ERROR;

/**
 * Security 模块的 Service 实现
 *
 * @author Luo Fei
 * @date 2023/03/24
 */
@Service
public class SecurityServiceImpl implements SecurityService {

    private static final String CSRF_TOKEN_ATTRIBUTE = "_csrf";

    @Override
    public CustomCsrfToken getCsrfToken(HttpServletRequest request) {
        String tokenValue;
        try {
            CsrfToken csrfToken = (CsrfToken) request.getAttribute(CSRF_TOKEN_ATTRIBUTE);
            tokenValue = csrfToken.getToken();
        } catch (Exception e) {
            String message = "CsrfToken not found, message: " + e.getMessage();
            throw new ServerException(SERVER_ERROR, message);
        }
        CustomCsrfToken customCsrfToken = new CustomCsrfToken();
        customCsrfToken.setTokenValue(tokenValue);
        return customCsrfToken;
    }

}
