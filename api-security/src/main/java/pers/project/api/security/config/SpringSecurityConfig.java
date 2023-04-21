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
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.context.DelegatingSecurityContextRepository;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestHandler;
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import static com.baomidou.mybatisplus.core.toolkit.StringPool.SLASH;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder.BCryptVersion;
import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;
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
@RequiredArgsConstructor
public class SpringSecurityConfig {

    /**
     * BCRYPT 密码强度
     * <p>
     * 有效值从 4 到 31，默认值为 10。强度越大，散列密码所需要做的工作越多。
     *
     * @see BCryptPasswordEncoder
     */
    private static final int BCRYPT_STRENGTH = 10;

    /**
     * BCrypt 密码编码器
     * <p>
     * 注意：从 {@code  BCryptPasswordEncoder#getSalt()} 方法可知，
     * 如果在 {@code BCryptPasswordEncoder} 构造函数中指定 {@code SecureRandom}，
     * 则它只会使用单例的 {@code SecureRandom}。
     *
     * @see <a href="https://springdoc.cn/spring-security/features/authentication/password-storage.html#authentication-password-storage-configuration">
     * 密码存储配置</a>
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(BCryptVersion.$2Y, BCRYPT_STRENGTH);
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
     * Spring Security 上下文存储策略
     * <p>
     * 这里只是把默认实现暴露为 Bean 来显式保存上下文信息。
     *
     * @see <a href="https://springdoc.cn/spring-security/servlet/authentication/persistence.html#securitycontextrepository">
     * SecurityContextRepository</a>
     * @see <a href="https://springdoc.cn/spring-security/migration/servlet/session-management.html#_%E8%A6%81%E6%B1%82%E6%98%8E%E7%A1%AE%E4%BF%9D%E5%AD%98_securitycontextrepository">
     * 要求明确保存 SecurityContextRepository</a>
     */
    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new DelegatingSecurityContextRepository(
                new RequestAttributeSecurityContextRepository(),
                new HttpSessionSecurityContextRepository()
        );
    }

    /**
     * 自定义 WebSecurity 的回调接口
     * <p>
     * 用于配置 Spring Security 忽略的资源请求，仅在测试环境生效。
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

        private final SecurityContextRepository securityContextRepository;
        private final RememberMeServices rememberMeServices;

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            // 配置 Spring Security 上下文
            http.securityContext()
                    .securityContextRepository(securityContextRepository);
            // 配置记住我功能
            http.rememberMe()
                    .rememberMeServices(rememberMeServices);
            // 用户未登录时允许访问的路径
            RequestMatcher[] permittedRequestMatchers = {
                    // 写在这里让 IDEA 可以导航到 Controller 方法
                    antMatcher(GET, "/csrf/token"),
                    antMatcher(POST, "/account/registry"),
                    antMatcher(POST, "/details/login"),
                    antMatcher(POST, "/verification/code")
            };
            // 配置请求授权
            http.authorizeHttpRequests()
                    .requestMatchers(permittedRequestMatchers)
                    .permitAll();
            // TODO: 2023/4/20 配置不生效
            http.authorizeHttpRequests()
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
         * CSRF 令牌请求处理程序
         *
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
         * Cookie CSRF 令牌存储库
         *
         * @see <a href="https://springdoc.cn/spring-security/servlet/exploits/csrf.html#servlet-csrf-configure">自定义 CsrfTokenRepository</a>
         */
        private CookieCsrfTokenRepository cookieCsrfTokenRepository() {
            CookieCsrfTokenRepository tokenRepository
                    = CookieCsrfTokenRepository.withHttpOnlyFalse();
            tokenRepository.setCookiePath(SLASH);
            return tokenRepository;
        }

    }

}
