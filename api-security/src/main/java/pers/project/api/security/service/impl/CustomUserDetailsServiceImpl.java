package pers.project.api.security.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dreamlu.mica.core.utils.WebUtil;
import net.dreamlu.mica.ip2region.core.Ip2regionSearcher;
import net.dreamlu.mica.ip2region.core.IpInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.session.security.web.authentication.SpringSessionRememberMeServices;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import pers.project.api.common.exception.BusinessException;
import pers.project.api.common.model.dto.LoginUserDTO;
import pers.project.api.common.model.security.CustomUserDetails;
import pers.project.api.common.util.bean.BeanCopierUtils;
import pers.project.api.security.mapper.UserAccountMapper;
import pers.project.api.security.mapper.UserProfileMapper;
import pers.project.api.security.model.dto.PhoneOrEmailLoginDTO;
import pers.project.api.security.model.dto.VerificationCodeCheckDTO;
import pers.project.api.security.model.po.UserAccountPO;
import pers.project.api.security.model.po.UserProfilePO;
import pers.project.api.security.service.CustomUserDetailsService;
import pers.project.api.security.service.SecurityService;

import java.time.LocalDateTime;
import java.util.function.Function;

import static com.baomidou.mybatisplus.core.toolkit.StringPool.TRUE;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.springframework.session.security.web.authentication.SpringSessionRememberMeServices.REMEMBER_ME_LOGIN_ATTR;
import static pers.project.api.common.enumeration.ErrorEnum.DATABASE_ERROR;
import static pers.project.api.common.enumeration.ErrorEnum.LOGIN_ERROR;
import static pers.project.api.security.config.SpringSessionConfig.SpringSecurityIntegrationConfig.REMEMBER_ME_SESSION_VALIDITY_SECONDS;
import static pers.project.api.security.enumeration.VerificationStrategyEnum.PHONE;

/**
 * Spring Security 加载用户特定数据的自定义 Service 实现
 *
 * @author Luo Fei
 * @date 2023/03/21
 * @see <a href="https://springdoc.cn/spring-security/servlet/authentication/passwords/user-details-service.html">
 * UserDetailsService<a/>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsServiceImpl implements CustomUserDetailsService {

    private final HttpServletRequest request;

    private final HttpServletResponse response;

    private final UserAccountMapper userAccountMapper;

    private final UserProfileMapper userProfileMapper;

    private final SecurityService securityService;

    private final Ip2regionSearcher ip2regionSearcher;

    private final RememberMeServices rememberMeServices;

    private final SecurityContextRepository securityContextRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (StringUtils.isBlank(username)) {
            /*
             * 该异常默认在 AbstractUserDetailsAuthenticationProvider.authenticate
             * 中被隐藏，转为抛出 BadCredentialsException。
             */
            throw new UsernameNotFoundException(EMPTY);
        }
        // 查询用户账号
        UserAccountPO userAccountPO = getLoginUserAccount(UserAccountPO::getUsername, username);
        if (userAccountPO == null) {
            throw new UsernameNotFoundException(EMPTY);
        }
        // 查询用户资料
        UserProfilePO userProfilePO = getLoginUserProfileByAccountId(userAccountPO.getId());
        if (userProfilePO == null) {
            /*
             * 其他异常也会在 DaoAuthenticationProvider.retrieveUser
             * 中被包装为 InternalAuthenticationServiceException。
             */
            String message = "UserProfile not found, accountId: " + userAccountPO.getId();
            throw new InternalAuthenticationServiceException(message);
        }
        // 复制属性
        return copyLoginUserProperties(userAccountPO, userProfilePO);
    }

    @Override
    public CustomUserDetails getLoginUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // instanceof 可以判空
        if (authentication instanceof UsernamePasswordAuthenticationToken) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof CustomUserDetails userDetails) {
                return userDetails;
            }
        }
        throw new BusinessException(LOGIN_ERROR, "用户未登录！");
    }

    @Override
    public LoginUserDTO getLoginUserDTO() {
        CustomUserDetails userDetails = getLoginUserDetails();
        LoginUserDTO loginUserDTO = new LoginUserDTO();
        BeanCopierUtils.copy(userDetails, loginUserDTO);
        return loginUserDTO;
    }

    @Override
    public void updateLoginUserDetails(CustomUserDetails newUserDetails) {
        // 更新存储在 Redis 中的 SecurityContext
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = UsernamePasswordAuthenticationToken.authenticated
                (newUserDetails, newUserDetails.getPassword(), newUserDetails.getAuthorities());
        securityContext.setAuthentication(authentication);
        securityContextRepository.saveContext(securityContext, request, response);
    }

    @Override
    public LoginUserDTO loginByPhoneOrEmail(PhoneOrEmailLoginDTO loginDTO) {
        // 检查验证码
        VerificationCodeCheckDTO codeCheckDTO = new VerificationCodeCheckDTO();
        BeanCopierUtils.copy(loginDTO, codeCheckDTO);
        boolean isVerified = securityService.checkVerificationCode(codeCheckDTO);
        boolean isUsingPhone = PHONE.name().equals(loginDTO.getStrategy());
        if (!isVerified) {
            String argument = isUsingPhone ? "手机号" : "邮箱";
            throw new BusinessException(LOGIN_ERROR, "%s或验证码不正确！".formatted(argument));
        }
        // 查询用户账号
        UserAccountPO userAccountPO = isUsingPhone ?
                getLoginUserAccount(UserAccountPO::getPhoneNumber, loginDTO.getPhoneNumber())
                : getLoginUserAccount(UserAccountPO::getEmailAddress, loginDTO.getEmailAddress());
        Function<String, BusinessException> getException = message -> {
            log.error(message);
            return new BusinessException(DATABASE_ERROR, "服务器错误，请联系管理员！");
        };
        if (userAccountPO == null) {
            String message = "UserAccount not found, phoneNumber: " + loginDTO.getPhoneNumber();
            throw getException.apply(message);
        }
        // 查询用户资料
        UserProfilePO userProfilePO = getLoginUserProfileByAccountId(userAccountPO.getId());
        if (userProfilePO == null) {
            String message = "UserProfile not found, email: " + loginDTO.getEmailAddress();
            throw getException.apply(message);
        }
        // 复制属性
        CustomUserDetails userDetails = copyLoginUserProperties(userAccountPO, userProfilePO);
        // 更新用户 IP 信息
        updateLoginUserIpInfo(userDetails);
        // 更新登录状态
        updateLoginUserDetails(userDetails);
        // 是否使用记住我服务
        if (TRUE.equals(loginDTO.getRememberMe())) {
            useRememberMeService();
        }
        // 返回登录用户信息
        LoginUserDTO loginUserDTO = new LoginUserDTO();
        BeanCopierUtils.copy(userDetails, loginUserDTO);
        return loginUserDTO;
    }

    @Override
    public void updateLoginUserIpInfo(CustomUserDetails userDetails) {
        // 先明确需求，此次登录会覆盖上一次的信息
        Assert.notNull(request, "The request should be not null");
        String ipAddress = WebUtil.getIP(request);
        // logFailure 是延迟加载的
        Runnable logFailure = () -> {
            if (log.isWarnEnabled()) {
                String message = """
                        Update login user IP info failed, accountId: %s, \
                        ipAddress: %s, remoteAddr: %s, remoteHost: %s
                        """.formatted(userDetails.getAccountId(), ipAddress,
                        request.getRemoteAddr(), request.getRemoteHost());
                log.warn(message);
            }
        };
        String ipOrigin = null;
        if (ipAddress == null) {
            // 找不到 ipAddress，日志记录
            logFailure.run();
        } else {
            // 找到 ipAddress，尝试找 ipOrigin
            try {
                ipOrigin = ip2regionSearcher.getInfo(ipAddress, IpInfo::getProvince);
            } catch (IllegalArgumentException e) {
                // 找不到 ipOrigin，日志记录
                logFailure.run();
            }
        }
        // 更新数据库会覆盖之前的信息
        LocalDateTime now = LocalDateTime.now();
        LambdaUpdateChainWrapper<UserProfilePO> updateWrapper
                = new LambdaUpdateChainWrapper<>(userProfileMapper);
        updateWrapper.set(UserProfilePO::getIpAddress, ipAddress)
                .set(UserProfilePO::getIpLocation, ipOrigin)
                .set(UserProfilePO::getLastLoginTime, now)
                .eq(UserProfilePO::getId, userDetails.getProfileId());
        updateWrapper.update();
        // 更新传入的 loginUserProfile
        userDetails.setIpAddress(ipAddress);
        userDetails.setIpLocation(ipOrigin);
        userDetails.setLastLoginTime(now);
    }

    /**
     * 获取登录用户的帐户信息
     *
     * @param getter {@code UserAccount} 的 Getter 方法引用
     * @param value  用于查找账户的值（账户名、邮箱、手机号等），与 {@code getter} 对应
     * @return 如果获取到帐户信息，则将其返回，否则返回 {@code null}。
     */
    private <F, V> UserAccountPO getLoginUserAccount(SFunction<UserAccountPO, F> getter, V value) {
        LambdaQueryWrapper<UserAccountPO> accountQueryWrapper = new LambdaQueryWrapper<>();
        accountQueryWrapper.select(UserAccountPO::getId, UserAccountPO::getUsername,
                UserAccountPO::getPassword, UserAccountPO::getAuthority, UserAccountPO::getSecretId,
                UserAccountPO::getAccountStatus);
        accountQueryWrapper.eq(getter, value);
        return userAccountMapper.selectOne(accountQueryWrapper);
    }

    /**
     * 按帐户 ID 获取用户资料
     *
     * @param accountId 用户账户 ID
     * @return 如果获取到用户资料，则将其返回，否则返回 {@code null}。
     */
    private UserProfilePO getLoginUserProfileByAccountId(String accountId) {
        LambdaQueryWrapper<UserProfilePO> profileQueryWrapper = new LambdaQueryWrapper<>();
        profileQueryWrapper.select(UserProfilePO::getId, UserProfilePO::getAvatar,
                UserProfilePO::getNickname, UserProfilePO::getWebsite, UserProfilePO::getGithub,
                UserProfilePO::getGitee, UserProfilePO::getBiography);
        profileQueryWrapper.eq(UserProfilePO::getAccountId, accountId);
        return userProfileMapper.selectOne(profileQueryWrapper);
    }

    /**
     * 复制登录用户属性
     *
     * @param userAccountPO 用户账户
     * @param userProfilePO 用户资料
     * @return 自定义 Spring Security 用户详细信息
     */
    private CustomUserDetails copyLoginUserProperties(UserAccountPO userAccountPO, UserProfilePO userProfilePO) {
        CustomUserDetails customUserDetails = new CustomUserDetails();
        BeanCopierUtils.copy(userAccountPO, customUserDetails);
        BeanCopierUtils.copy(userProfilePO, customUserDetails);
        customUserDetails.setAccountId(userAccountPO.getId());
        customUserDetails.setProfileId(userProfilePO.getId());
        customUserDetails.setLastLoginTime(LocalDateTime.now());
        return customUserDetails;
    }

    /**
     * 使用 Remember Me 服务
     * <p>
     * 在 {@link SecurityContextRepository} 保存 {@code SecurityContext} 后调用，
     * 用户将使用 Spring Security 得 Remember Me 功能。
     *
     * @see AbstractAuthenticationProcessingFilter#successfulAuthentication
     * @see SpringSessionRememberMeServices#loginSuccess
     */
    @SuppressWarnings("all") // 抑制 JavaDoc 警告
    private void useRememberMeService() {
        request.setAttribute(REMEMBER_ME_LOGIN_ATTR, true);
        request.getSession().setMaxInactiveInterval(REMEMBER_ME_SESSION_VALIDITY_SECONDS);
    }

}
