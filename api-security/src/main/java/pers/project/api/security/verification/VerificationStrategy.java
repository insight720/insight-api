package pers.project.api.security.verification;

import pers.project.api.security.execption.VerificationContextException;

/**
 * 验证策略接口
 *
 * @author Luo Fei
 * @date 2023/04/16
 */
public interface VerificationStrategy {

    /**
     * 发送验证码
     *
     * @param contextInfo 上下文信息（如邮箱号、手机号等）
     * @throws VerificationContextException 如果发送出现错误
     */
    void sendVerificationCode(String contextInfo) throws VerificationContextException;

    /**
     * 检查验证码
     *
     * @param contextInfo          上下文信息（如邮箱号、手机号等）
     * @param userVerificationCode 用户输入的验证码
     * @return 如果检查通过，则返回 {@code true}，否则返回 {@code false}。
     * @throws VerificationContextException 如果验证出现错误
     */
    boolean checkVerificationCode(String contextInfo, String userVerificationCode) throws VerificationContextException;

}
