package pers.project.api.security.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import pers.project.api.common.model.dto.LoginUserDTO;
import pers.project.api.common.model.security.CustomUserDetails;
import pers.project.api.security.model.dto.PhoneOrEmailLoginDTO;

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
     * 获取 Spring Security 登录用户详细信息
     *
     * @return 登录用户详细信息（不为 null）
     */
    CustomUserDetails getLoginUserDetails();

    /**
     * 获取登录用户 DTO
     *
     * @return 登录用户 DTO
     */
    LoginUserDTO getLoginUserDTO();

    /**
     * 更新 Spring Security 登录用户详细信息
     *
     * @param newUserDetails 新的用户详细信息
     */
    void updateLoginUserDetails(CustomUserDetails newUserDetails);

    /**
     * 手机号或邮箱登录
     *
     * @param loginDTO 手机或邮箱登录的 DTO
     * @return 登录用户 DTO
     */
    LoginUserDTO loginByPhoneOrEmail(PhoneOrEmailLoginDTO loginDTO);

    /**
     * 更新登录用户 IP 信息
     *
     * @param userDetails 自定义 Spring Security 用户详细信息
     * @implNote <pre>
     * 每次登录都会覆盖式更新上一次登录的信息。
     * 这里的更新有两层含义：
     * 1. 更新数据库存储的 IP 信息。
     * 2. 更新传入的 {@code userDetails} 的 IP 信息。
     * </pre>
     */
    void updateLoginUserIpInfo(CustomUserDetails userDetails);

}
