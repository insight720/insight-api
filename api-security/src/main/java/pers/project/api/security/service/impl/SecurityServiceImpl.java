package pers.project.api.security.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Service;
import pers.project.api.common.exception.BusinessException;
import pers.project.api.security.enumeration.VerificationStrategyEnum;
import pers.project.api.security.execption.VerificationContextException;
import pers.project.api.security.model.dto.VerificationCodeCheckDTO;
import pers.project.api.security.model.dto.VerificationCodeSendingDTO;
import pers.project.api.security.service.SecurityService;
import pers.project.api.security.verification.VerificationContext;

import static pers.project.api.common.enumeration.ErrorEnum.SERVER_ERROR;
import static pers.project.api.security.enumeration.VerificationStrategyEnum.*;

/**
 * Security 模块的 Service 实现
 *
 * @author Luo Fei
 * @date 2023/03/24
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SecurityServiceImpl implements SecurityService {

    /**
     * CSRF Token 请求属性名
     */
    private static final String CSRF_TOKEN_ATTRIBUTE = "_csrf";

    private final VerificationContext verificationContext;

    @Override
    public void loadDeferredCsrfToken(HttpServletRequest request) {
        Object attribute = request.getAttribute(CSRF_TOKEN_ATTRIBUTE);
        // instanceof 可以判空
        if (attribute instanceof CsrfToken csrfToken) {
            // 必须调用，否则 CsrfToken 因 Lambda 表达式延迟加载，不会生成 Cookie
            csrfToken.getToken();
            return;
        }
        throw new IllegalStateException("Should never get here");
    }

    @Override
    public void sendVerificationCode(VerificationCodeSendingDTO codeSendingDTO) {
        // 根据数据确定验证策略
        String phoneNumber = codeSendingDTO.getPhoneNumber();
        String email = codeSendingDTO.getEmail();
        if (phoneNumber == null && email == null) {
            log.error("Both phone number and email address are null");
            throw new BusinessException(SERVER_ERROR, "服务器错误，验证码发送失败！");
        }
        String strategy = codeSendingDTO.getStrategy();
        VerificationStrategyEnum strategyEnum = VerificationStrategyEnum.valueOf(strategy);
        // 使用确定的策略发送验证码
        try {
            switch (strategyEnum) {
                case PHONE -> verificationContext.sendCredential(phoneNumber, PHONE);
                case EMAIL -> verificationContext.sendCredential(email, EMAIL);
            }
        } catch (VerificationContextException e) {
            if (log.isWarnEnabled()) {
                log.warn("""
                        Failed to get verification code, codeSendingDTO: %s
                        """.formatted(codeSendingDTO), e);
            }
            throw new BusinessException(SERVER_ERROR, "获取验证码失败，请稍后再试！");
        }
    }

    @Override
    public boolean checkVerificationCode(VerificationCodeCheckDTO codeCheckDTO) {
        String phoneNumber = codeCheckDTO.getPhoneNumber();
        String email = codeCheckDTO.getEmail();
        if (phoneNumber == null && email == null) {
            log.error("Both phone number and email address are null");
            throw new BusinessException(SERVER_ERROR, "服务器错误，请联系管理员！");
        }
        VerificationStrategyEnum strategyEnum = valueOf(codeCheckDTO.getStrategy());
        boolean isVerified;
        try {
            String verificationCode = codeCheckDTO.getVerificationCode();
            isVerified = switch (strategyEnum) {
                case PHONE -> verificationContext.verifyCredential
                        (phoneNumber, verificationCode, PHONE);
                case EMAIL -> verificationContext.verifyCredential
                        (email, verificationCode, EMAIL);
            };
        } catch (VerificationContextException e) {
            if (log.isWarnEnabled()) {
                log.warn("""
                        Failed to verify verification code, codeCheckDTO: %s
                        """.formatted(codeCheckDTO), e);
            }
            throw new BusinessException(SERVER_ERROR, "服务器错误，请稍后再试！");
        }
        return isVerified;
    }

}
