package pers.project.api.security.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;
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
     * @see XorCsrfTokenRequestAttributeHandler#handle(HttpServletRequest, HttpServletResponse, Supplier)
     */
    void loadDeferredCsrfToken(HttpServletRequest request);

    /**
     * 发送验证码
     *
     * @param codeSendingDTO 验证码发送 DTO
     */
    void sendVerificationCode(VerificationCodeSendingDTO codeSendingDTO);

    /**
     * 检查验证码是否正确。
     *
     * @param codeCheckDTO 验证码检查 DTO
     * @return 如果验证码检查通过，返回 {@code true}，否则返回 {@code false}。
     */
    boolean checkVerificationCode(VerificationCodeCheckDTO codeCheckDTO);

}
