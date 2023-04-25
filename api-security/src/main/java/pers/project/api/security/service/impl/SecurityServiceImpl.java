package pers.project.api.security.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import pers.project.api.common.exception.BusinessException;
import pers.project.api.security.enumeration.VerificationStrategyEnum;
import pers.project.api.security.execption.VerificationContextException;
import pers.project.api.security.model.dto.VerificationCodeCheckDTO;
import pers.project.api.security.model.dto.VerificationCodeSendingDTO;
import pers.project.api.security.service.SecurityService;
import pers.project.api.security.verification.VerificationContext;

import java.util.function.Supplier;

import static com.baomidou.mybatisplus.core.toolkit.StringPool.COMMA;
import static pers.project.api.common.enumeration.ErrorEnum.SERVER_ERROR;
import static pers.project.api.common.enumeration.ErrorEnum.VERIFICATION_CODE_ERROR;
import static pers.project.api.security.enumeration.VerificationStrategyEnum.EMAIL;
import static pers.project.api.security.enumeration.VerificationStrategyEnum.PHONE;

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

    private static final String DEFAULT_CSRF_PARAMETER_NAME = "_csrf";

    private final VerificationContext verificationContext;

    @Override
    public void loadDeferredCsrfToken(HttpServletRequest request) {
        Object attribute = request.getAttribute(DEFAULT_CSRF_PARAMETER_NAME);
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
        Supplier<BusinessException> supplierForIllegalState
                = () -> new BusinessException(SERVER_ERROR, "服务器错误，验证码发送失败");
        String identifier = determineVerificationIdentifier(supplierForIllegalState,
                codeSendingDTO.getPhoneNumber(), codeSendingDTO.getEmailAddress());
        VerificationStrategyEnum strategyEnum = VerificationStrategyEnum.valueOf(codeSendingDTO.getStrategy());
        try {
            switch (strategyEnum) {
                case PHONE -> verificationContext.sendCredential(identifier, PHONE);
                case EMAIL -> verificationContext.sendCredential(identifier, EMAIL);
            }
        } catch (VerificationContextException e) {
            warnVerificationContextException(identifier, strategyEnum, e);
            throw supplierForIllegalState.get();
        }
    }

    @Override
    public boolean checkLoginVerificationCode(String loginIdentifier, String verificationCode, String strategy) {
        VerificationStrategyEnum strategyEnum
                = EnumUtils.getEnum(VerificationStrategyEnum.class, strategy);
        Assert.state(strategy != null, "The strategyEnum must be found");
        boolean isValid;
        try {
            isValid = isValidVerificationCode(loginIdentifier, verificationCode, strategyEnum);
        } catch (VerificationContextException e) {
            warnVerificationContextException(loginIdentifier, strategyEnum, e);
            throw new IllegalStateException(e);
        }
        return isValid;
    }

    @Override
    public VerificationStrategyEnum checkVerificationCode(VerificationCodeCheckDTO codeCheckDTO,
                                                          Supplier<BusinessException> supplierForInvalidCode) {
        Supplier<BusinessException> supplierForIllegalState
                = () -> new BusinessException(SERVER_ERROR, "服务器错误，验证失败");
        if (supplierForInvalidCode == null) {
            supplierForInvalidCode = () -> new BusinessException(VERIFICATION_CODE_ERROR, "验证码错误");
        }
        String identifier = determineVerificationIdentifier(supplierForIllegalState,
                codeCheckDTO.getEmailAddress(), codeCheckDTO.getEmailAddress());
        VerificationStrategyEnum strategyEnum = VerificationStrategyEnum.valueOf(codeCheckDTO.getStrategy());
        boolean isValid;
        try {
            isValid = isValidVerificationCode(identifier, codeCheckDTO.getVerificationCode(), strategyEnum);
        } catch (VerificationContextException e) {
            warnVerificationContextException(identifier, strategyEnum, e);
            throw supplierForInvalidCode.get();
        }
        if (!isValid) {
            throw supplierForInvalidCode.get();
        }
        return strategyEnum;
    }

    private <E extends RuntimeException> String determineVerificationIdentifier(Supplier<E> supplierForIllegalState,
                                                                                String... identifiers) {
        String identifier = ObjectUtils.firstNonNull(identifiers);
        if (identifier == null) {
            log.warn("All verification identifiers are null: {}", StringUtils.join(identifiers, COMMA));
            throw supplierForIllegalState.get();
        }
        return identifier;
    }

    private boolean isValidVerificationCode(String identifier,
                                            String verificationCode,
                                            VerificationStrategyEnum strategyEnum)
            throws VerificationContextException {
        boolean isValid;
        isValid = switch (strategyEnum) {
            case PHONE -> verificationContext.verifyCredential
                    (identifier, verificationCode, PHONE);
            case EMAIL -> verificationContext.verifyCredential
                    (identifier, verificationCode, EMAIL);
        };
        return isValid;
    }

    private void warnVerificationContextException(String identifier,
                                                  VerificationStrategyEnum strategyEnum,
                                                  Exception e) {
        if (log.isWarnEnabled()) {
            String argument = strategyEnum.equals(PHONE) ?
                    "phone number" : "email address";
            log.warn("""
                    A verification context failure occurred, %s: %s
                    """.formatted(argument, identifier), e);
        }
    }

}
