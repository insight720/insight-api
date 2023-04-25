package pers.project.api.security.verification;

import pers.project.api.security.enumeration.VerificationStrategyEnum;
import pers.project.api.security.execption.VerificationContextException;

/**
 * 验证上下文
 *
 * @author Luo Fei
 * @date 2023/04/16
 */
public interface VerificationContext {

    /**
     * 发送凭据（如验证码）
     *
     * @param contextInfo  上下文信息（如手机号、邮箱号等）
     * @param strategyEnum 验证策略枚举
     * @throws VerificationContextException 发送出现错误
     */
    void sendCredential(String contextInfo, VerificationStrategyEnum strategyEnum) throws VerificationContextException;

    /**
     * 验证凭据（如验证码）
     *
     * @param contextInfo 上下文信息（如手机号、邮箱号等）
     * @param credential  凭据
     * @return 如果验证通过，则返回 {@code true}，否则返回 {@code false}。
     * @throws VerificationContextException 验证出现错误
     */
    boolean verifyCredential(String contextInfo, String credential,
                             VerificationStrategyEnum strategyEnum) throws VerificationContextException;

}
