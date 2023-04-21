package pers.project.api.security.execption;

import lombok.experimental.StandardException;
import pers.project.api.security.verification.VerificationContext;

/**
 * 验证上下文异常
 * <p>
 * 对 {@link VerificationContext} 产生异常的包装。
 *
 * @author Luo Fei
 * @date 2023/04/16
 */
@StandardException
public class VerificationContextException extends Exception {
}
