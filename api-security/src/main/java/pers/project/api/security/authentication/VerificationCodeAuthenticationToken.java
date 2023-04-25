package pers.project.api.security.authentication;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.util.Assert;
import pers.project.api.security.enumeration.VerificationStrategyEnum;

import java.io.Serial;
import java.util.Collection;
import java.util.Objects;

/**
 * 验证码认证令牌
 * <p>
 * 请参照 {@link UsernamePasswordAuthenticationToken}。
 *
 * @author Luo Fei
 * @date 2023/04/23
 * @see VerificationStrategyEnum
 * @see VerificaionCodeAuthenticationProvider
 * @see VerificationCodeAuthenticationFilter
 * @see <a href="https://springdoc.cn/spring-security/servlet/authentication/architecture.html#servlet-authentication-authentication">
 * Authentication</a>
 */
public class VerificationCodeAuthenticationToken extends AbstractAuthenticationToken {

    @Serial
    private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

    // region Field
    private final Object principal;

    private Object credentials;

    private final Object strategy;
    // endregion

    // region Constructor
    public VerificationCodeAuthenticationToken(Object principal, Object credentials, Object strategy) {
        super(null);
        this.principal = principal;
        this.credentials = credentials;
        this.strategy = strategy;
        setAuthenticated(false);
    }

    public VerificationCodeAuthenticationToken(Object principal, Object credentials, Object strategy,
                                               Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.credentials = credentials;
        this.strategy = strategy;
        // must use super, as we override
        super.setAuthenticated(true);
    }
    // endregion

    // region Factory method
    public static VerificationCodeAuthenticationToken unauthenticated(Object principal, Object credentials, Object strategy) {
        return new VerificationCodeAuthenticationToken(principal, credentials, strategy);
    }

    public static VerificationCodeAuthenticationToken authenticated(Object principal, Object credentials, Object strategy,
                                                                    Collection<? extends GrantedAuthority> authorities) {
        return new VerificationCodeAuthenticationToken(principal, credentials, strategy, authorities);
    }
    // endregion

    // region Overriding method
    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        Assert.isTrue(!isAuthenticated,
                "Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
        super.setAuthenticated(false);
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    @Override
    public Object getCredentials() {
        return credentials;
    }

    @Override
    public void eraseCredentials() {
        super.eraseCredentials();
        this.credentials = null;
    }
    // endregion

    // region Getter
    public Object getStrategy() {
        return strategy;
    }
    // endregion

    // region Equals and HashCode
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        VerificationCodeAuthenticationToken that = (VerificationCodeAuthenticationToken) obj;
        return isAuthenticated() == that.isAuthenticated()
               && Objects.equals(getStrategy(), that.getStrategy())
               && Objects.equals(getPrincipal(), that.getPrincipal())
               && Objects.equals(getCredentials(), that.getCredentials())
               && Objects.equals(getDetails(), that.getDetails())
               && Objects.equals(getAuthorities(), that.getAuthorities());
    }

    @Override
    public int hashCode() {
        return Objects.hash(isAuthenticated(), getStrategy(), getPrincipal(),
                getCredentials(), getDetails(), getAuthorities());
    }
    // endregion

}
