package pers.project.api.security.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pers.project.api.common.constant.enumeration.ErrorEnum;
import pers.project.api.common.exception.ServerException;
import pers.project.api.common.model.dto.LoginUserDTO;
import pers.project.api.common.model.security.CustomUserDetails;
import pers.project.api.security.mapper.UserAccountMapper;
import pers.project.api.security.mapper.UserProfileMapper;
import pers.project.api.security.model.entity.UserAccount;
import pers.project.api.security.model.entity.UserProfile;
import pers.project.api.security.service.CustomUserDetailsService;

import static org.apache.commons.lang3.StringUtils.EMPTY;

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

    private final UserAccountMapper userAccountMapper;
    private final UserProfileMapper userProfileMapper;

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
        profileQueryWrapper.select(UserProfile::getId, UserProfile::getNickname,
                UserProfile::getAvatar, UserProfile::getBiography);
        profileQueryWrapper.eq(UserProfile::getAccountId, userAccount.getId());
        UserProfile userProfile = userProfileMapper.selectOne(profileQueryWrapper);
        if (userProfile == null) {
            String message = "UserProfile not found, accountId: " + userAccount.getId();
            throw new ServerException(ErrorEnum.DATABASE_ERROR, message);
        }
        // 复制属性
        CustomUserDetails customUserDetails = new CustomUserDetails();
        BeanUtils.copyProperties(userAccount, customUserDetails);
        BeanUtils.copyProperties(userProfile, customUserDetails);
        customUserDetails.setAccountId(userAccount.getId());
        customUserDetails.setProfileId(userProfile.getId());
        return customUserDetails;
    }
    @Override
    public LoginUserDTO getLoginUserDetails(HttpServletRequest request) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        if (authentication == null) {
            throw new ServerException(ErrorEnum.SERVICE_ERROR, "用户未登录");
        }
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        if (userDetails == null) {
            throw new ServerException(ErrorEnum.SERVICE_ERROR, "用户未登录");
        }
        LoginUserDTO loginUserDTO = new LoginUserDTO();
        BeanUtils.copyProperties(userDetails, loginUserDTO);
        return loginUserDTO;
    }

}
