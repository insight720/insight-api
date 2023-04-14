package pers.project.api.security.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;
import pers.project.api.common.exception.BusinessException;
import pers.project.api.common.model.dto.LoginUserDTO;
import pers.project.api.common.model.security.CustomUserDetails;
import pers.project.api.common.util.bean.BeanCopierUtils;
import pers.project.api.security.mapper.UserAccountMapper;
import pers.project.api.security.mapper.UserProfileMapper;
import pers.project.api.security.model.entity.UserAccount;
import pers.project.api.security.model.entity.UserProfile;
import pers.project.api.security.service.CustomUserDetailsService;

import java.time.LocalDateTime;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static pers.project.api.common.enumeration.ErrorEnum.LOGIN_ERROR;

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

    private final Ip2regionSearcher ip2regionSearcher;

    private final UserAccountMapper userAccountMapper;

    private final UserProfileMapper userProfileMapper;

    private final SecurityContextRepository securityContextRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (StringUtils.isBlank(username)) {
            // 该异常默认被隐藏，抛出 BadCredentialsException
            throw new UsernameNotFoundException(EMPTY);
        }
        // 查询用户账号
        LambdaQueryWrapper<UserAccount> accountQueryWrapper = new LambdaQueryWrapper<>();
        accountQueryWrapper.select(UserAccount::getId, UserAccount::getUsername,
                UserAccount::getPassword, UserAccount::getAuthority, UserAccount::getAccountKey,
                UserAccount::getAccountStatus);
        accountQueryWrapper.eq(UserAccount::getUsername, username);
        UserAccount userAccount = userAccountMapper.selectOne(accountQueryWrapper);
        if (userAccount == null) {
            throw new UsernameNotFoundException(EMPTY);
        }
        // 查询用户资料
        LambdaQueryWrapper<UserProfile> profileQueryWrapper = new LambdaQueryWrapper<>();
        profileQueryWrapper.select(UserProfile::getId, UserProfile::getAvatar,
                UserProfile::getNickname, UserProfile::getWebsite, UserProfile::getGithub,
                UserProfile::getGitee, UserProfile::getBiography);
        profileQueryWrapper.eq(UserProfile::getAccountId, userAccount.getId());
        UserProfile userProfile = userProfileMapper.selectOne(profileQueryWrapper);
        if (userProfile == null) {
            String message = "UserProfile not found, accountId: " + userAccount.getId();
            throw new InternalAuthenticationServiceException(message);
        }
        // 获取用户 IP 信息
        String ipAddress = WebUtil.getIP(request);
        String ipOrigin = null;
        try {
            ipOrigin = ip2regionSearcher.getInfo(ipAddress, IpInfo::getProvince);
        } catch (IllegalArgumentException e) {
            if (log.isWarnEnabled()) {
                String message = """
                        Search ipOrigin failed, accountId: %s, remoteAddr: %s, ipAddress: %s
                        """.formatted(userAccount.getId(), request.getRemoteAddr(), ipAddress);
                log.warn(message, e);
            }
        }
        // 复制属性
        CustomUserDetails customUserDetails = new CustomUserDetails();
        BeanCopierUtils.copy(userAccount, customUserDetails);
        BeanCopierUtils.copy(userProfile, customUserDetails);
        customUserDetails.setAccountId(userAccount.getId());
        customUserDetails.setProfileId(userProfile.getId());
        customUserDetails.setIpAddress(ipAddress);
        customUserDetails.setIpOrigin(ipOrigin);
        customUserDetails.setLastLoginTime(LocalDateTime.now());
        return customUserDetails;
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
        throw new BusinessException(LOGIN_ERROR, "用户未登录");
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

}
