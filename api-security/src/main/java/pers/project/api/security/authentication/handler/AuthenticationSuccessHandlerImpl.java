package pers.project.api.security.authentication.handler;

import com.alibaba.fastjson2.JSON;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import pers.project.api.common.model.dto.LoginUserDTO;
import pers.project.api.common.model.security.CustomUserDetails;
import pers.project.api.common.util.ResultUtils;
import pers.project.api.security.service.CustomUserDetailsService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Spring Security 认证成功处理程序
 *
 * @author Luo Fei
 * @date 2023/03/21
 * @see AuthenticationSuccessHandler
 */
@Component
@RequiredArgsConstructor
public class AuthenticationSuccessHandlerImpl implements AuthenticationSuccessHandler {

    private final CustomUserDetailsService userDetailsService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        // 更新登录用户 IP 信息
        userDetailsService.updateLoginUserIpInfo(userDetails);
        // 返回登录用户信息
        LoginUserDTO loginUserDTO = new LoginUserDTO();
        BeanUtils.copyProperties(userDetails, loginUserDTO);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        String jsonString = JSON.toJSONString(ResultUtils.success(loginUserDTO));
        response.getWriter().write(jsonString);
    }

}
