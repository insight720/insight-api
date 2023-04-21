package pers.project.api.common.model.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;


/**
 * 自定义 Spring Security 用户详细信息
 *
 * @author Luo Fei
 * @date 2023/03/21
 * @see <a href="https://springdoc.cn/spring-security/servlet/authentication/passwords/user-details.html">
 * UserDetails</a>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomUserDetails implements UserDetails {

    // region Same with UserAccount
    /**
     * 账号
     */
    private String username;
    /**
     * 密码
     */
    private String password;
    /**
     * 邮箱
     */
    private String emailAddress;
    /**
     * 手机号
     */
    private String phoneNumber;
    /**
     * 权限
     */
    private String authority;
    /**
     * 密钥 ID
     */
    private String secretId;
    /**
     * 账号状态
     */
    private Integer accountStatus;
    // endregion

    /**
     * 用户资料主键
     */
    private String profileId;

    // region Same with UserProfile
    /**
     * 账户主键
     */
    private String accountId;
    /**
     * 头像
     */
    private String avatar;
    /**
     * 昵称
     */
    private String nickname;
    /**
     * 个人网站
     */
    private String website;
    /**
     * GitHub
     */
    private String github;
    /**
     * Gitee
     */
    private String gitee;
    /**
     * 个人简介
     */
    private String biography;
    /**
     * IP 地址
     */
    private String ipAddress;
    /**
     * IP 属地
     */
    private String ipLocation;
    /**
     * 上次登录时间
     */
    private LocalDateTime lastLoginTime;
    // endregion

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new CustomizedGrantedAuthority(authority));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
