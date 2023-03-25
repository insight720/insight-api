package pers.project.api.security.handler;

import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

/**
 * Spring Security 授权管理器
 *
 * @author Luo Fei
 * @date 2023/03/21
 * @see <a href="https://springdoc.cn/spring-security/servlet/authorization/architecture.html#_authorizationmanager">
 * AuthorizationManager</a>
 */
@Component
public class CustomizedAuthorizationManagerImpl implements CustomizedAuthorizationManager {

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication,
                                       RequestAuthorizationContext object) {
        return new AuthorizationDecision(true);
    }

}
