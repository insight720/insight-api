package pers.project.api.security.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import pers.project.api.common.model.dto.LoginUserDTO;
import pers.project.api.common.model.security.CustomUserDetails;
import pers.project.api.security.execption.PrincipalNotFoundException;

/**
 * Spring Security 加载用户特定数据的自定义 Service
 *
 * @author Luo Fei
 * @date 2022/03/21
 * @see <a href="https://springdoc.cn/spring-security/servlet/authentication/passwords/user-details-service.html">
 * UserDetailsService<a/>
 */
public interface CustomUserDetailsService extends UserDetailsService {

    /**
     * 获取登录用户 DTO
     *
     * @return 登录用户 DTO
     */
    LoginUserDTO getLoginUserDTO();

    /**
     * 获取 Spring Security 登录用户详细信息
     *
     * @return 登录用户详细信息（不为 null）
     */
    CustomUserDetails getLoginUserDetails();

    /**
     * 更新 Spring Security 登录用户详细信息
     *
     * @param newUserDetails 新的用户详细信息
     */
    void updateLoginUserDetails(CustomUserDetails newUserDetails);

    /**
     * 加载用于验证码登录的用户信息
     *
     * @param loginIdentifier 登录标识（手机号、邮箱地址等）
     * @param strategy        验证策略
     *                        <p>
     *                        （{@code VerificationStrategyEnum} 枚举常量名）
     * @return 用户详细信息（不为 null）
     * @throws PrincipalNotFoundException 如果找不到用户。
     */
    CustomUserDetails loadUserForVerificationCodeLogin(String loginIdentifier, String strategy);

    /**
     * 更新登录用户 IP 信息
     *
     * @param userDetails 自定义 Spring Security 用户详细信息
     * @implNote <pre>
     * 每次登录都会覆盖式更新上一次登录的信息。
     * 这里的更新有三层含义：
     * 1. 更新数据库存储的 IP 信息。
     * 2. 更新传入的 {@code userDetails} 的 IP 信息。
     * 3. 更新 Session 存储的 IP 信息。
     * </pre>
     */
    void updateLoginUserIpInfo(CustomUserDetails userDetails);

}
