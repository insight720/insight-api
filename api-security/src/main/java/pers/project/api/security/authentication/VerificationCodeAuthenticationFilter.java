package pers.project.api.security.authentication;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.springframework.http.HttpMethod.POST;

/**
 * 验证码认证过滤器
 * <p>
 * 请参照 {@link UsernamePasswordAuthenticationFilter}。
 *
 * @author Luo Fei
 * @date 2023/04/23
 * @see <a href="https://springdoc.cn/spring-security/servlet/authentication/architecture.html#servlet-authentication-authentication">
 * AbstractAuthenticationProcessingFilter</a>
 */
@SuppressWarnings("unused")
public class VerificationCodeAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    // region Static field
    public static final String SPRING_SECURITY_FORM_LOGIN_IDENTIFIER_KEY = "loginIdentifier";

    public static final String SPRING_SECURITY_FORM_VERIFICATION_CODE_KEY = "verificationCode";

    public static final String SPRING_SECURITY_FORM_STRATEGY_KEY = "strategy";

    private static final AntPathRequestMatcher DEFAULT_ANT_PATH_REQUEST_MATCHER
            = AntPathRequestMatcher.antMatcher(POST, "/verification/code/login");
    // endregion

    // region Property
    private String loginIdentifierParameter = SPRING_SECURITY_FORM_LOGIN_IDENTIFIER_KEY;

    private String verificationCodeParameter = SPRING_SECURITY_FORM_VERIFICATION_CODE_KEY;

    private String strategyParameter = SPRING_SECURITY_FORM_STRATEGY_KEY;

    private boolean postOnly = true;
    // endregion

    // region Constructor
    public VerificationCodeAuthenticationFilter() {
        super(DEFAULT_ANT_PATH_REQUEST_MATCHER);
    }

    public VerificationCodeAuthenticationFilter(String defaultFilterProcessesUrl) {
        super(defaultFilterProcessesUrl);
    }

    public VerificationCodeAuthenticationFilter(RequestMatcher requiresAuthenticationRequestMatcher) {
        super(requiresAuthenticationRequestMatcher);
    }
    // endregion

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        if (this.postOnly && !request.getMethod().equals(POST.name())) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }
        String loginIdentifier = obtainLoginIdentifier(request);
        loginIdentifier = (loginIdentifier != null) ? loginIdentifier.trim() : EMPTY;
        String verificationCode = obtainVerificationCode(request);
        verificationCode = (verificationCode != null) ? verificationCode.trim() : EMPTY;
        String strategy = obtainStrategy(request);
        strategy = (strategy != null) ? strategy.trim() : EMPTY;
        VerificationCodeAuthenticationToken authRequest = VerificationCodeAuthenticationToken
                .unauthenticated(loginIdentifier, verificationCode, strategy);
        // Allow subclasses to set the "details" property
        setDetails(request, authRequest);
        return this.getAuthenticationManager().authenticate(authRequest);
    }

    // region Overridable method
    protected String obtainLoginIdentifier(HttpServletRequest request) {
        return request.getParameter(loginIdentifierParameter);
    }

    protected String obtainVerificationCode(HttpServletRequest request) {
        return request.getParameter(verificationCodeParameter);
    }

    protected String obtainStrategy(HttpServletRequest request) {
        return request.getParameter(strategyParameter);
    }

    protected void setDetails(HttpServletRequest request, VerificationCodeAuthenticationToken authRequest) {
        authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
    }
    // endregion


    // region Getter and Setter
    public String getLoginIdentifierParameter() {
        return loginIdentifierParameter;
    }

    public void setLoginIdentifierParameter(String loginIdentifierParameter) {
        this.loginIdentifierParameter = loginIdentifierParameter;
    }

    public String getVerificationCodeParameter() {
        return verificationCodeParameter;
    }

    public void setVerificationCodeParameter(String verificationCodeParameter) {
        this.verificationCodeParameter = verificationCodeParameter;
    }

    public String getStrategyParameter() {
        return strategyParameter;
    }

    public void setStrategyParameter(String strategyParameter) {
        this.strategyParameter = strategyParameter;
    }

    public void setPostOnly(boolean postOnly) {
        this.postOnly = postOnly;
    }
    // endregion

}
