package pers.project.api.security.handler;

import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;

/**
 * 自定义 Spring Security 授权管理器接口
 *
 * @author Luo Fei
 * @date 2023/03/21
 * @see <a href="https://springdoc.cn/spring-security/servlet/authorization/architecture.html#_authorizationmanager">
 * AuthorizationManager</a>
 */
public interface CustomizedAuthorizationManager
        extends AuthorizationManager<RequestAuthorizationContext> {

}
