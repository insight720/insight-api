package pers.project.api.security.handler;

import com.alibaba.fastjson2.JSON;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import pers.project.api.common.constant.UserConst;
import pers.project.api.common.model.dto.LoginUserDTO;
import pers.project.api.common.model.security.CustomUserDetails;
import pers.project.api.common.util.ResultUtils;

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
public class AuthenticationSuccessHandlerImpl implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        LoginUserDTO loginUserDTO = new LoginUserDTO();
        BeanUtils.copyProperties(userDetails, loginUserDTO);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(JSON.toJSONString(ResultUtils.success(loginUserDTO)));

        request.getSession().setAttribute(UserConst.USER_LOGIN_STATE, loginUserDTO);
    }

}
