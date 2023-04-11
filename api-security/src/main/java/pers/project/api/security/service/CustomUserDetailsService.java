package pers.project.api.security.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import pers.project.api.common.model.dto.LoginUserDTO;
import pers.project.api.common.model.security.CustomUserDetails;

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

}
