package pers.project.api.security.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;
import pers.project.api.common.exception.BusinessException;
import pers.project.api.security.authentication.VerificaionCodeAuthenticationProvider;
import pers.project.api.security.enumeration.VerificationStrategyEnum;
import pers.project.api.security.model.dto.VerificationCodeCheckDTO;
import pers.project.api.security.model.dto.VerificationCodeSendingDTO;

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
     * @see XorCsrfTokenRequestAttributeHandler#handle
     */
    void loadDeferredCsrfToken(HttpServletRequest request);

    /**
     * 发送验证码
     *
     * @param codeSendingDTO 验证码发送 DTO
     */
    void sendVerificationCode(VerificationCodeSendingDTO codeSendingDTO);

    /**
     * 检查登录验证码是否正确
     *
     * @param loginIdentifier  登录标识（手机号、邮箱地址等）
     * @param verificationCode 6 位数字验证码
     * @param strategy         验证策略
     *                         <p>
     *                         （{@code VerificationStrategyEnum} 枚举常量名）
     * @return 如果验证码检查通过，返回 {@code true}，否则返回 {@code false}。
     * @throws IllegalStateException 未知状态错误
     * @see VerificaionCodeAuthenticationProvider#additionalAuthenticationChecks
     */
    // Suppress JavaDoc Warnings
    @SuppressWarnings("all")
    boolean checkLoginVerificationCode(String loginIdentifier, String verificationCode, String strategy);

    /**
     * 检查验证码
     * <p>
     * 在检查不通过时抛出指定的 {@code BusinessException} 。
     *
     * @param codeCheckDTO           验证码检查 DTO
     * @param supplierForInvalidCode 检查不通过时抛出异常的提供者
     *                               （{@code null} 表示抛出默认异常）
     * @return 验证策略枚举（帮助基于策略的程序）
     * @throws BusinessException 业务异常（带有提示信息）
     */
    VerificationStrategyEnum checkVerificationCode(VerificationCodeCheckDTO codeCheckDTO,
                                                   Supplier<BusinessException> supplierForInvalidCode);

}
