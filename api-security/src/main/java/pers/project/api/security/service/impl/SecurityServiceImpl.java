package pers.project.api.security.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;
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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

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
                codeCheckDTO.getPhoneNumber(), codeCheckDTO.getEmailAddress());
        VerificationStrategyEnum strategyEnum = VerificationStrategyEnum.valueOf(codeCheckDTO.getStrategy());
        boolean isValid;
        try {
            isValid = isValidVerificationCode(identifier, codeCheckDTO.getVerificationCode(), strategyEnum);
        } catch (VerificationContextException e) {
            warnVerificationContextException(identifier, strategyEnum, e);
            throw supplierForIllegalState.get();
        }
        if (!isValid) {
            throw supplierForInvalidCode.get();
        }
        return strategyEnum;
    }

    /**
     * 确定验证标识符（手机号或邮箱地址）。
     *
     * @param supplierForIllegalState 当验证标识符无效时，使用此供应程序生成异常。
     * @param identifiers             验证标识符的可变参数列表。
     * @return 如果验证标识符有效，则返回验证标识符；否则抛出异常。
     * @throws E 如果验证标识符无效，则抛出 {@code supplierForIllegalState} 生成的异常。
     */
    private <E extends RuntimeException> String determineVerificationIdentifier(Supplier<E> supplierForIllegalState,
                                                                                String... identifiers) {

        List<String> nonNullIdentifiers = new ArrayList<>(2);
        for (String identifier : identifiers) {
            if (identifier != null) {
                nonNullIdentifiers.add(identifier);
            }
        }
        if (nonNullIdentifiers.size() != 1) {
            log.warn("There is not only one identifier that is not null");
            throw supplierForIllegalState.get();
        }
        return nonNullIdentifiers.get(0);
    }

    /**
     * 验证验证码是否有效。
     *
     * @param identifier       验证标识符，可以是电话号码或电子邮件地址。
     * @param verificationCode 验证码。
     * @param strategyEnum     发送验证码的策略。
     * @return 如果验证码是有效的，则返回 {@code true}；否则返回 {@code false}。
     * @throws VerificationContextException 当验证过程出现错误时，会抛出此异常。
     */
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

    /**
     * 记录验证上下文异常的日志。
     *
     * @param identifier   验证标识符，可以是电话号码或电子邮件地址。
     * @param strategyEnum 发送验证码的策略。
     * @param e            验证上下文异常。
     */
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
