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
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.internal.constraintvalidators.bv.EmailValidator;
import org.springframework.beans.BeanUtils;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import pers.project.api.common.exception.BusinessException;
import pers.project.api.common.model.dto.LoginUserDTO;
import pers.project.api.common.model.security.CustomUserDetails;
import pers.project.api.common.validation.validator.SensitiveWordValidator;
import pers.project.api.security.authentication.VerificationCodeAuthenticationToken;
import pers.project.api.security.enumeration.VerificationStrategyEnum;
import pers.project.api.security.execption.PrincipalNotFoundException;
import pers.project.api.security.mapper.UserAccountMapper;
import pers.project.api.security.mapper.UserProfileMapper;
import pers.project.api.security.model.po.UserAccountPO;
import pers.project.api.security.model.po.UserProfilePO;
import pers.project.api.security.service.CustomUserDetailsService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.baomidou.mybatisplus.core.toolkit.StringPool.COMMA;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.splitByWholeSeparator;
import static pers.project.api.common.enumeration.ErrorEnum.LOGIN_ERROR;
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

    public static final Pattern PHONE_NUMBER_PATTERN = Pattern.compile("^\\+861[3-9]\\d{9}$");

    public static final EmailValidator EMAIL_ADDRESS_VALIDATOR = new EmailValidator();

    private final HttpServletRequest request;

    private final HttpServletResponse response;

    private final UserAccountMapper userAccountMapper;

    private final UserProfileMapper userProfileMapper;

    private final Ip2regionSearcher ip2regionSearcher;

    private final SecurityContextRepository securityContextRepository;

    @Override
    public LoginUserDTO getLoginUserDTO() {
        CustomUserDetails userDetails = getLoginUserDetails();
        LoginUserDTO loginUserDTO = new LoginUserDTO();
        BeanUtils.copyProperties(userDetails, loginUserDTO);
        return loginUserDTO;
    }

    @Override
    public CustomUserDetails getLoginUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // instanceof 可以判空
        if (authentication instanceof UsernamePasswordAuthenticationToken
            || authentication instanceof VerificationCodeAuthenticationToken) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof CustomUserDetails userDetails) {
                return userDetails;
            }
        }
        throw new BusinessException(LOGIN_ERROR, "用户未登录");
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
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 校验用户名
        boolean isValid = validateUsername(username);
        if (!isValid) {
            if (log.isWarnEnabled()) {
                log.warn("Invalid username: {}", username);
            }
            throw new UsernameNotFoundException(EMPTY);
        }
        // 查询用户账号
        UserAccountPO userAccountPO = getLoginUserAccount(UserAccountPO::getUsername, username);
        if (userAccountPO == null) {
            throw new UsernameNotFoundException(EMPTY);
        }
        // 查询用户资料
        UserProfilePO userProfilePO = getLoginUserProfileByAccountId(userAccountPO.getId());
        // 复制属性
        return copyLoginUserProperties(userAccountPO, userProfilePO);
    }

    @Override
    public CustomUserDetails loadUserForVerificationCodeLogin(String loginIdentifier, String strategy) {
        // 确认校验策略
        VerificationStrategyEnum strategyEnum = EnumUtils.getEnum(VerificationStrategyEnum.class, strategy);
        if (strategyEnum == null) {
            log.warn("Invalid strategy: {}", strategy);
            throw new PrincipalNotFoundException(EMPTY);
        }
        // 校验请求参数（在此校验的原因是注解校验抛出的不会被全局异常处理器捕获）
        boolean isUsingPhone = PHONE.equals(strategyEnum);
        boolean isValid = isUsingPhone ?
                PHONE_NUMBER_PATTERN.matcher(loginIdentifier).matches()
                // EmailValidator 默认允许 null 和 ""
                : StringUtils.isNotBlank(loginIdentifier)
                  && EMAIL_ADDRESS_VALIDATOR.isValid(loginIdentifier, null);
        if (!isValid) {
            if (log.isWarnEnabled()) {
                String argument = isUsingPhone ? "phone number" : "email address";
                log.warn("Invalid {}: {}", argument, loginIdentifier);
            }
            throw new PrincipalNotFoundException(EMPTY);
        }
        // 查询用户账号
        UserAccountPO userAccountPO = isUsingPhone ?
                getLoginUserAccount(UserAccountPO::getPhoneNumber, loginIdentifier)
                : getLoginUserAccount(UserAccountPO::getEmailAddress, loginIdentifier);
        if (userAccountPO == null) {
            throw new PrincipalNotFoundException(EMPTY);
        }
        // 查询用户资料
        UserProfilePO userProfilePO = getLoginUserProfileByAccountId(userAccountPO.getId());
        return copyLoginUserProperties(userAccountPO, userProfilePO);
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
                        Search login user IP info failed, accountId: %s, \
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
            } catch (IllegalArgumentException ignored) {
                // 抛出异常 ipOrigin 即为 null
            }
            if (ipOrigin == null) {
                // 找不到 ipOrigin，日志记录
                logFailure.run();
            }
        }
        // 更新数据库会覆盖之前的信息
        LocalDateTime now = LocalDateTime.now();
        LambdaUpdateChainWrapper<UserProfilePO>
                updateWrapper = new LambdaUpdateChainWrapper<>(userProfileMapper);
        updateWrapper.set(UserProfilePO::getIpAddress, ipAddress)
                .set(UserProfilePO::getIpLocation, ipOrigin)
                .set(UserProfilePO::getLastLoginTime, now)
                .eq(UserProfilePO::getId, userDetails.getProfileId());
        updateWrapper.update();
        // 更新传入的 userDetails
        userDetails.setIpAddress(ipAddress);
        userDetails.setIpLocation(ipOrigin);
        userDetails.setLastLoginTime(now);
        // 更新 Session 存储的 IP 信息
        updateLoginUserDetails(userDetails);
    }

    /**
     * 验证给定的用户名是否有效。
     *
     * @param username 要验证的用户名
     * @return 如果用户名有效，则返回 true；否则返回 false
     */
    private boolean validateUsername(String username) {
        if (StringUtils.isBlank(username)) {
            return false;
        }
        int length = username.length();
        if (length < 3 || length > 25) {
            return false;
        }
        return !SensitiveWordValidator.LENIENT_WORD_BS.contains(username);
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
        accountQueryWrapper.select(UserAccountPO::getId, UserAccountPO::getUsername, UserAccountPO::getPassword,
                UserAccountPO::getEmailAddress, UserAccountPO::getPhoneNumber, UserAccountPO::getAuthority,
                UserAccountPO::getSecretId, UserAccountPO::getAccountStatus);
        accountQueryWrapper.eq(getter, value);
        return userAccountMapper.selectOne(accountQueryWrapper);
    }

    /**
     * 按帐户 ID 获取用户资料
     *
     * @param accountId 用户账户 ID
     * @return 如果获取到用户资料，则将其返回，否则返回 {@code null}。
     * @throws InternalAuthenticationServiceException 如果找不到用户资料。则抛出该异常。
     */
    private UserProfilePO getLoginUserProfileByAccountId(String accountId) {
        LambdaQueryWrapper<UserProfilePO> profileQueryWrapper = new LambdaQueryWrapper<>();
        profileQueryWrapper.select(UserProfilePO::getId, UserProfilePO::getAvatar,
                UserProfilePO::getNickname, UserProfilePO::getWebsite, UserProfilePO::getGithub,
                UserProfilePO::getGitee, UserProfilePO::getBiography);
        profileQueryWrapper.eq(UserProfilePO::getAccountId, accountId);
        UserProfilePO userProfilePO = userProfileMapper.selectOne(profileQueryWrapper);
        if (userProfilePO == null) {
            String message = "UserProfile not found, accountId: " + accountId;
            throw new InternalAuthenticationServiceException(message);
        }
        return userProfilePO;
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
        BeanUtils.copyProperties(userAccountPO, customUserDetails);
        BeanUtils.copyProperties(userProfilePO, customUserDetails);
        customUserDetails.setAccountId(userAccountPO.getId());
        customUserDetails.setProfileId(userProfilePO.getId());
        // 将账户权限转为权限集合
        Set<String> athroritySet = Arrays.stream(splitByWholeSeparator
                        (userAccountPO.getAuthority(), COMMA))
                .collect(Collectors.toUnmodifiableSet());
        customUserDetails.setAuthoritySet(athroritySet);
        return customUserDetails;
    }

}
