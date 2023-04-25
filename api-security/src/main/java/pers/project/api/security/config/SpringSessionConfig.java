package pers.project.api.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.session.config.annotation.web.http.SpringHttpSessionConfiguration;
import org.springframework.session.data.redis.RedisIndexedSessionRepository;
import org.springframework.session.security.SpringSessionBackedSessionRegistry;
import org.springframework.session.security.web.authentication.SpringSessionRememberMeServices;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;
import org.springframework.stereotype.Component;

import static com.baomidou.mybatisplus.core.toolkit.StringPool.SLASH;
import static org.springframework.session.security.web.authentication.SpringSessionRememberMeServices.REMEMBER_ME_LOGIN_ATTR;

/**
 * Spring Session 配置类
 *
 * @author Luo Fei
 * @date 2023/04/21
 */
@Component
public class SpringSessionConfig {

    /**
     * Cookie 序列化器
     * <p>
     * 将阻止 {@link SpringHttpSessionConfiguration#afterPropertiesSet()} 配置默认的序列化器。
     *
     * @return cookie 序列化器
     */
    @Bean
    public CookieSerializer cookieSerializer() {
        DefaultCookieSerializer cookieSerializer = new DefaultCookieSerializer();
        cookieSerializer.setCookiePath(SLASH);
        /*
         * 支持 Spring Security 的 Remember Me 功能
         * 请参阅: DefaultCookieSerializer.getMaxAge
         *        SpringHttpSessionConfiguration.createDefaultCookieSerializer
         */
        cookieSerializer.setRememberMeRequestAttribute(REMEMBER_ME_LOGIN_ATTR);
        return cookieSerializer;
    }

    /**
     * Spring Security 整合配置类
     */
    @Component
    public static class SpringSecurityIntegrationConfig {

        /**
         * Remember Me 功能的 Session 有效时间（单位：秒）
         */
        public static final int REMEMBER_ME_SESSION_VALIDITY_SECONDS = 7 * 24 * 60 * 60;

        /**
         * HTTP 会话事件的发布者
         * <p>
         * 暴露为 Spring Bean 来保持对会话生命周期事件的更新。
         *
         * @see <a href="https://springdoc.cn/spring-security/servlet/authentication/session-management.html#ns-concurrent-sessions">
         * Session 的并发控制</a>
         */
        @Bean
        public HttpSessionEventPublisher httpSessionEventPublisher() {
            return new HttpSessionEventPublisher();
        }

        /**
         * 会话注册表
         *
         * @see <a href="https://springdoc.cn/spring-security/servlet/authentication/session-management.html#list-authenticated-principals">
         * SessionRegistry</a>
         * <p>
         * <a href="https://docs.spring.io/spring-session/reference/spring-security.html#spring-security-concurrent-sessions">
         * Spring Security Concurrent Session Control</a>
         */
        @Bean
        public SessionRegistry sessionRegistry(RedisIndexedSessionRepository sessionRepository) {
            return new SpringSessionBackedSessionRegistry<>(sessionRepository);
        }

        /**
         * Remember Me 服务
         *
         * @see <a href="https://docs.spring.io/spring-session/reference/spring-security.html#spring-security-rememberme">
         * Spring Security Remember-me Support</a>
         */
        @Bean
        public RememberMeServices rememberMeServices() {
            SpringSessionRememberMeServices services = new SpringSessionRememberMeServices();
            services.setValiditySeconds(REMEMBER_ME_SESSION_VALIDITY_SECONDS);
            return services;
        }

    }

}
