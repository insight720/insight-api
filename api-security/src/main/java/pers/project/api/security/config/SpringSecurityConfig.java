package pers.project.api.security.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestHandler;
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.session.data.redis.RedisIndexedSessionRepository;
import org.springframework.session.security.SpringSessionBackedSessionRegistry;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static pers.project.api.security.constant.AuthorityConst.ROLE_USER;


/**
 * Spring Security 配置类
 *
 * @author Luo Fei
 * @date 2022/03/21
 * @see <a href="https://springdoc.cn/spring-security/">
 * Spring Security 中文文档</a>
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SpringSecurityConfig {

    /**
     * 密码编码器
     *
     * @see <a href="https://springdoc.cn/spring-security/features/authentication/password-storage.html#authentication-password-storage-configuration">
     * 密码存储配置</a>
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 会话注册表
     * <p>
     * Spring Session 的实现无法使用 getAllPrincipals()。
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
     * 自定义 WebSecurity 的回调接口
     * <p>
     * 用于配置 Spring Security 忽略的资源请求。
     *
     * @see WebSecurity
     */
    @Bean
    @Profile("dev")
    public WebSecurityCustomizer ignoringCustomizer() {
        return (web) -> web.ignoring().requestMatchers(new String[]{
                "/doc.html",
                "/v3/api-docs",
                "/v3/api-docs/*",
                "/webjars/js/*",
                "/webjars/css/*",
        });
    }

    @Configuration
    @RequiredArgsConstructor
    public static class SecurityFilterChainConfig {

        private static final RequestMatcher[] PERMITTED_REQUEST_MATCHERS = {
                AntPathRequestMatcher.antMatcher(GET, "/csrf"),
                AntPathRequestMatcher.antMatcher(POST, "/account/registry"),
        };

        // region CSRF  防护
        private static final String CSRF_COOKIE_PATH = "/";
        // endregion

        // region 会话管理
        public static final int MAXIMUM_SESSIONS = 10;
        private final SessionRegistry sessionRegistry;
        // endregion

        // region 认证
        public static final String LOGIN_PROCESSING_URL = "/login";
        public static final String LOGOUT_URL = "/logout";
        private final AuthenticationSuccessHandler authenticationSuccessHandler;
        private final AuthenticationFailureHandler authenticationFailureHandler;
        private final LogoutSuccessHandler logoutSuccessHandler;
        private final AuthenticationEntryPoint authenticationEntryPoint;
        // endregion

        // region 授权
        private final AccessDeniedHandler accessDeniedHandler;
        // endregion

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            // 配置请求授权
            http.authorizeHttpRequests()
                    .requestMatchers(PERMITTED_REQUEST_MATCHERS)
                    .permitAll()
                    .anyRequest()
                    .hasAuthority(ROLE_USER);
            // 配置 CSRF 防护
            http.csrf().csrfTokenRepository(cookieCsrfTokenRepository())
                    .csrfTokenRequestHandler(csrfTokenRequestHandler());
            // 配置会话管理
            http.sessionManagement()
                    .maximumSessions(MAXIMUM_SESSIONS)
                    .sessionRegistry(sessionRegistry);
            // 配置表单登录
            http.formLogin()
                    .loginProcessingUrl(LOGIN_PROCESSING_URL)
                    .successHandler(authenticationSuccessHandler)
                    .failureHandler(authenticationFailureHandler);
            // 配置登出
            http.logout()
                    .logoutUrl(LOGOUT_URL)
                    .logoutSuccessHandler(logoutSuccessHandler);
            // 配置认证异常和授权异常处理
            http.exceptionHandling()
                    .authenticationEntryPoint(authenticationEntryPoint)
                    .accessDeniedHandler(accessDeniedHandler);
            return http.build();
        }

        /**
         * @see <a href="https://docs.spring.io/spring-security/reference/5.8/migration/servlet/exploits.html#_i_am_using_angularjs_or_another_javascript_framework">I am using AngularJS or another Javascript framework</a>
         * @see <a href="https://github.com/spring-projects/spring-security/issues/12915">Issue</a>
         */
        @SuppressWarnings("all")
        private CsrfTokenRequestHandler csrfTokenRequestHandler() {
            // 使用 XorCsrfTokenRequestAttributeHandler 的 handle 方法
            XorCsrfTokenRequestAttributeHandler delegate = new XorCsrfTokenRequestAttributeHandler();
            // 但使用 CsrfTokenRequestHandler 默认的 resolveCsrfTokenValue 方法
            CsrfTokenRequestHandler csrfTokenRequestHandler = delegate::handle;
            return csrfTokenRequestHandler;
        }

        /**
         * @see <a href="https://springdoc.cn/spring-security/servlet/exploits/csrf.html#servlet-csrf-configure">自定义CsrfTokenRepository</a>
         */
        private CookieCsrfTokenRepository cookieCsrfTokenRepository() {
            CookieCsrfTokenRepository tokenRepository
                    = CookieCsrfTokenRepository.withHttpOnlyFalse();
            tokenRepository.setCookiePath(CSRF_COOKIE_PATH);
            return tokenRepository;
        }

    }

}
