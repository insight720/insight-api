package pers.project.api.security.authentication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.authentication.*;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.security.core.userdetails.cache.NullUserCache;
import org.springframework.util.Assert;
import pers.project.api.security.enumeration.VerificationStrategyEnum;
import pers.project.api.security.execption.PrincipalNotFoundException;
import pers.project.api.security.service.CustomUserDetailsService;
import pers.project.api.security.service.SecurityService;


/**
 * 验证码身份验证提供程序
 * <p>
 * 请参照 {@link DaoAuthenticationProvider}。
 *
 * @author Luo Fei
 * @date 2023/04/22
 * @see VerificationCodeAuthenticationToken
 * @see VerificationCodeAuthenticationFilter
 * @see <a href="https://springdoc.cn/spring-security/servlet/authentication/architecture.html#servlet-authentication-authenticationprovider">
 * AuthenticationProvider</a>
 */
@SuppressWarnings("unused")
public class VerificaionCodeAuthenticationProvider
        implements AuthenticationProvider, InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(VerificaionCodeAuthenticationProvider.class);

    private UserCache userCache = new NullUserCache();

    private boolean forcePrincipalAsString = false;

    private boolean forceStrategyAsString = false;

    protected boolean hidePrincipalNotFoundExceptions = true;

    private UserDetailsChecker preAuthenticationChecks = new DefaultPreAuthenticationChecks();

    private UserDetailsChecker postAuthenticationChecks = new DefaultPostAuthenticationChecks();

    private GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

    private SecurityService securityService;

    private CustomUserDetailsService userDetailsService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Assert.isInstanceOf(VerificationCodeAuthenticationToken.class, authentication,
                "Only VerificationCodeAuthenticationToken is supported");
        return authenticate((VerificationCodeAuthenticationToken) authentication);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return VerificationCodeAuthenticationToken.class.isAssignableFrom(authentication);
    }

    @Override
    public void afterPropertiesSet() {
        Assert.notNull(this.userDetailsService, "A UserDetailsService must be set");
        Assert.notNull(this.securityService, "A SecurityService must be set");
    }

    protected Authentication authenticate(VerificationCodeAuthenticationToken authentication) {
        Object principal = authentication.getPrincipal();
        boolean cacheWasUsed = true;
        UserDetails user = this.userCache.getUserFromCache(principal.toString());
        if (user == null) {
            cacheWasUsed = false;
            try {
                user = retrieveUser(authentication);
            } catch (PrincipalNotFoundException ex) {
                logger.debug("Failed to find principal '" + principal + "'");
                if (!this.hidePrincipalNotFoundExceptions) {
                    throw ex;
                }
                throw new BadCredentialsException("Bad credentials");
            }
            Assert.notNull(user, "retrieveUser returned null - a violation of the interface contract");
        }
        try {
            this.preAuthenticationChecks.check(user);
            additionalAuthenticationChecks(authentication);
        } catch (AuthenticationException ex) {
            if (!cacheWasUsed) {
                throw ex;
            }
            // There was a problem, so try again after checking
            // we're using latest data (i.e. not from the cache)
            cacheWasUsed = false;
            user = retrieveUser(authentication);
            this.preAuthenticationChecks.check(user);
            additionalAuthenticationChecks(authentication);
        }
        this.postAuthenticationChecks.check(user);
        if (!cacheWasUsed) {
            this.userCache.putUserInCache(user);
        }
        Object principalToReturn = user;
        if (this.forcePrincipalAsString) {
            principalToReturn = user.getUsername();
        }
        String strategyAsString = authentication.getStrategy().toString();
        Object strategyToReturn = strategyAsString;
        if (!this.forceStrategyAsString) {
            strategyToReturn = VerificationStrategyEnum.valueOf(strategyAsString);
        }
        return createSuccessAuthentication
                (principalToReturn, strategyToReturn, authentication, user);
    }

    protected UserDetails retrieveUser(VerificationCodeAuthenticationToken token)
            throws AuthenticationException {
        try {
            UserDetails loadedUser = this.getUserDetailsService().loadUserForVerificationCodeLogin
                    (token.getPrincipal().toString(), token.getStrategy().toString());
            if (loadedUser == null) {
                throw new InternalAuthenticationServiceException(
                        "UserDetailsService returned null, which is an interface contract violation");
            }
            return loadedUser;
        } catch (PrincipalNotFoundException | InternalAuthenticationServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex);
        }
    }

    protected void additionalAuthenticationChecks
            (VerificationCodeAuthenticationToken token) throws AuthenticationException {
        Object credentials = token.getCredentials();
        if (credentials == null) {
            logger.debug("Failed to authenticate since no credentials provided");
            throw new BadCredentialsException("Bad credentials");
        }
        boolean isVerified;
        try {
            isVerified = securityService.checkLoginVerificationCode
                    (token.getPrincipal().toString(), credentials.toString(),
                            token.getStrategy().toString());
        } catch (Exception e) {
            logger.warn("Failed to authenticate since an unknown exception is thrown");
            throw new InternalAuthenticationServiceException(e.getMessage(), e);
        }
        if (!isVerified) {
            logger.debug("Failed to authenticate since verification code does not match stored value");
            throw new BadCredentialsException("Bad credentials");
        }
    }

    protected Authentication createSuccessAuthentication(Object principal, Object strategy,
                                                         VerificationCodeAuthenticationToken token,
                                                         UserDetails user) {
        VerificationCodeAuthenticationToken result = VerificationCodeAuthenticationToken
                .authenticated(principal, token.getCredentials(), strategy,
                        this.authoritiesMapper.mapAuthorities(user.getAuthorities()));
        result.setDetails(token.getDetails());
        logger.debug("Authenticated user");
        return result;
    }

    // region UserDetailsChecker
    protected static class DefaultPreAuthenticationChecks implements UserDetailsChecker {

        @Override
        public void check(UserDetails user) {
            if (!user.isAccountNonLocked()) {
                logger.debug("Failed to authenticate since user account is locked");
                throw new LockedException("User account is locked");
            }
            if (!user.isEnabled()) {
                logger.debug("Failed to authenticate since user account is disabled");
                throw new DisabledException("User is disabled");
            }
            if (!user.isAccountNonExpired()) {
                logger.debug("Failed to authenticate since user account has expired");
                throw new AccountExpiredException("User account has expired");
            }
        }

    }

    protected static class DefaultPostAuthenticationChecks implements UserDetailsChecker {

        @Override
        public void check(UserDetails user) {
            if (!user.isCredentialsNonExpired()) {
                logger.debug("Failed to authenticate since user account credentials have expired");
                throw new CredentialsExpiredException("User credentials have expired");
            }
        }

    }
    // endregion

    // region Getter and Setter
    public UserCache getUserCache() {
        return userCache;
    }

    public void setUserCache(UserCache userCache) {
        this.userCache = userCache;
    }

    public boolean isForcePrincipalAsString() {
        return forcePrincipalAsString;
    }

    public void setForcePrincipalAsString(boolean forcePrincipalAsString) {
        this.forcePrincipalAsString = forcePrincipalAsString;
    }

    public boolean isForceStrategyAsString() {
        return forceStrategyAsString;
    }

    public void setForceStrategyAsString(boolean forceStrategyAsString) {
        this.forceStrategyAsString = forceStrategyAsString;
    }

    public boolean isHidePrincipalNotFoundExceptions() {
        return hidePrincipalNotFoundExceptions;
    }

    public void setHidePrincipalNotFoundExceptions(boolean hidePrincipalNotFoundExceptions) {
        this.hidePrincipalNotFoundExceptions = hidePrincipalNotFoundExceptions;
    }

    public UserDetailsChecker getPreAuthenticationChecks() {
        return preAuthenticationChecks;
    }

    public void setPreAuthenticationChecks(UserDetailsChecker preAuthenticationChecks) {
        this.preAuthenticationChecks = preAuthenticationChecks;
    }

    public UserDetailsChecker getPostAuthenticationChecks() {
        return postAuthenticationChecks;
    }

    public void setPostAuthenticationChecks(UserDetailsChecker postAuthenticationChecks) {
        this.postAuthenticationChecks = postAuthenticationChecks;
    }

    public void setAuthoritiesMapper(GrantedAuthoritiesMapper authoritiesMapper) {
        this.authoritiesMapper = authoritiesMapper;
    }

    protected CustomUserDetailsService getUserDetailsService() {
        return this.userDetailsService;
    }

    public void setUserDetailsService(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    protected SecurityService getSecurityService() {
        return securityService;
    }

    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }
    // endregion

}
