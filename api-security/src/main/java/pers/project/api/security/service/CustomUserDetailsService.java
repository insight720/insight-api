package pers.project.api.security.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.userdetails.UserDetailsService;
import pers.project.api.common.model.dto.LoginUserDTO;

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
     * 获取登录用户详细信息
     *
     * @param request HTTP 请求
     * @return 登录用户信息
     */
    LoginUserDTO getLoginUserDetails(HttpServletRequest request);

}
