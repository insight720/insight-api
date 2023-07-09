package pers.project.api.security.execption;

import lombok.experimental.StandardException;
import org.springframework.security.core.AuthenticationException;

/**
 * 未找到主体异常
 * <p>
 * {@link AuthenticationException} 的子类。
 *
 * @author Luo Fei
 * @date 2023/07/06
 */
@StandardException
public class PrincipalNotFoundException extends AuthenticationException {
}
