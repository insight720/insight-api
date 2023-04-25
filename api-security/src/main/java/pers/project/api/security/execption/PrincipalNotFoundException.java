package pers.project.api.security.execption;

import org.springframework.security.core.AuthenticationException;

/**
 * @author Luo Fei
 * @date 2023/04/23
 */
public class PrincipalNotFoundException extends AuthenticationException {

    public PrincipalNotFoundException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public PrincipalNotFoundException(String msg) {
        super(msg);
    }

}
