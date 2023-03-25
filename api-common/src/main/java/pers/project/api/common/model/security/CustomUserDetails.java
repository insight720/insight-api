package pers.project.api.common.model.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

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
    private String email;
    /**
     * 手机号
     */
    private String phoneNumber;
    /**
     * 权限
     */
    private String authority;
    /**
     * 帐户密钥
     */
    private String accountKey;
    /**
     * 账号状态
     */
    private Integer accountStatus;
    // endregion

    /**
     * 用户资料主键
     */
    private Long profileId;

    // region Same with UserProfile
    /**
     * 账户主键
     */
    private Long accountId;
    /**
     * 昵称
     */
    private String nickname;
    /**
     * 头像
     */
    private String avatar;
    /**
     * 个人简介
     */
    private String biography;
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
