package pers.project.api.security.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.ProviderManager;
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
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.session.*;
import org.springframework.security.web.context.DelegatingSecurityContextRepository;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.csrf.*;
import org.springframework.security.web.util.matcher.RequestMatcher;
import pers.project.api.security.authentication.VerificaionCodeAuthenticationProvider;
import pers.project.api.security.authentication.VerificationCodeAuthenticationFilter;
import pers.project.api.security.service.CustomUserDetailsService;
import pers.project.api.security.service.SecurityService;

import java.util.List;

import static com.baomidou.mybatisplus.core.toolkit.StringPool.SLASH;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder.BCryptVersion;
import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;


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
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(BCryptVersion.$2Y, BCRYPT_STRENGTH);
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
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers(new String[]{
                "/doc.html",
                "/v3/api-docs",
                "/v3/api-docs/*",
                "/webjars/js/*",
                "/webjars/css/*",
        });
    }

    /**
     * CSRF 令牌请求处理程序
     *
     * @see <a href="https://docs.spring.io/spring-security/reference/5.8/migration/servlet/exploits.html#_i_am_using_angularjs_or_another_javascript_framework">I am using AngularJS or another Javascript framework</a>
     * @see <a href="https://github.com/spring-projects/spring-security/issues/12915">Issue</a>
     */
    // Suppress warnings for csrfTokenRequestHandler
    @SuppressWarnings("all")
    @Bean
    public CsrfTokenRequestHandler csrfTokenRequestHandler() {
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
    @Bean
    public CsrfTokenRepository csrfTokenRepository() {
        CookieCsrfTokenRepository tokenRepository
                = CookieCsrfTokenRepository.withHttpOnlyFalse();
        tokenRepository.setCookiePath(SLASH);
        return tokenRepository;
    }

    @Configuration
    @RequiredArgsConstructor
    public static class SecurityFilterChainConfig {

        // region Concurrent Session Control
        public static final int MAXIMUM_SESSIONS = 10;
        private final SessionRegistry sessionRegistry;
        // endregion

        // region Authentication
        public static final String LOGIN_PROCESSING_URL = "/login";
        public static final String LOGOUT_URL = "/logout";
        public static final String VERIFICATION_CODE_LOGIN_URL = "/verification/code/login";

        private final AuthenticationSuccessHandler authenticationSuccessHandler;
        private final AuthenticationFailureHandler authenticationFailureHandler;
        private final LogoutSuccessHandler logoutSuccessHandler;
        private final AuthenticationEntryPoint authenticationEntryPoint;

        private final RememberMeServices rememberMeServices;

        private final CustomUserDetailsService userDetailsService;

        private final SecurityService securityService;
        // endregion

        // region Authorization
        private final AccessDeniedHandler accessDeniedHandler;
        // endregion

        // region CSRF
        private final CsrfTokenRepository csrfTokenRepository;

        private final CsrfTokenRequestHandler csrfTokenRequestHandler;
        // endregion

        // region Security Context
        private final SecurityContextRepository securityContextRepository;
        // endregion

        // region Custom Security Filter

        /**
         * 验证码登录的认证提供者
         * <p>
         * 此提供者是自定义的。
         *
         * @see VerificaionCodeAuthenticationProvider
         */
        @Bean
        public VerificaionCodeAuthenticationProvider verificaionCodeAuthenticationProvider() {
            VerificaionCodeAuthenticationProvider provider = new VerificaionCodeAuthenticationProvider();
            provider.setUserDetailsService(userDetailsService);
            provider.setSecurityService(securityService);
            return provider;
        }

        @Bean
        public VerificationCodeAuthenticationFilter verificationCodeAuthenticationFilter() {
            VerificationCodeAuthenticationFilter filter
                    = new VerificationCodeAuthenticationFilter(VERIFICATION_CODE_LOGIN_URL);
            // 作为默认 ProviderManager 父级 AuthenticationManager
            ProviderManager parent = new ProviderManager(verificaionCodeAuthenticationProvider());
            filter.setAuthenticationManager(parent);
            // 设置以下内容来与 UsernamePasswordAuthenticationFilter 保持相同的上下文
            filter.setAuthenticationFailureHandler(authenticationFailureHandler);
            filter.setAuthenticationSuccessHandler(authenticationSuccessHandler);
            filter.setRememberMeServices(rememberMeServices);
            filter.setSecurityContextRepository(securityContextRepository);
            List<SessionAuthenticationStrategy> strategies = List.of(
                    new ConcurrentSessionControlAuthenticationStrategy(sessionRegistry),
                    new ChangeSessionIdAuthenticationStrategy(),
                    new RegisterSessionAuthenticationStrategy(sessionRegistry),
                    new CsrfAuthenticationStrategy(csrfTokenRepository)
            );
            SessionAuthenticationStrategy compositeStrategy
                    = new CompositeSessionAuthenticationStrategy(strategies);
            filter.setSessionAuthenticationStrategy(compositeStrategy);
            return filter;
        }
        // endregion

        // region SecurityFilterChain
        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            // 配置 Spring Security 上下文
            http.securityContext()
                    .securityContextRepository(securityContextRepository);
            // 用户未登录时允许访问的路径
            RequestMatcher[] permittedRequestMatchers = {
                    // 写在这里让 IDEA 可以导航到 Controller 方法
                    antMatcher(GET, "/csrf/token"),
                    antMatcher(POST, "/account/registry"),
                    antMatcher(POST, "/verification/code"),
                    antMatcher("/test"),
            };
            // 配置请求授权
            http.authorizeHttpRequests()
                    .requestMatchers(permittedRequestMatchers)
                    .permitAll()
                    .anyRequest()
                    .authenticated();
            // 配置 CSRF 防护
            http.csrf().csrfTokenRepository(csrfTokenRepository)
                    .csrfTokenRequestHandler(csrfTokenRequestHandler);
            // 配置会话管理
            http.sessionManagement()
                    .maximumSessions(MAXIMUM_SESSIONS)
                    .sessionRegistry(sessionRegistry);
            // 配置账户名登录及验证码登录
            http.formLogin()
                    .loginProcessingUrl(LOGIN_PROCESSING_URL)
                    .successHandler(authenticationSuccessHandler)
                    .failureHandler(authenticationFailureHandler);
            http.userDetailsService(userDetailsService)
                    .addFilterBefore(verificationCodeAuthenticationFilter(),
                            UsernamePasswordAuthenticationFilter.class)
                    // 此 Provider 将添加到默认 ProviderManager 中
                    .authenticationProvider(verificaionCodeAuthenticationProvider());
            // 配置记住我功能
            http.rememberMe()
                    .rememberMeServices(rememberMeServices);
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
        // endregion

    }

}
