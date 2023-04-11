package pers.project.api.security.web.authentication;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import pers.project.api.common.model.dto.LoginUserDTO;
import pers.project.api.common.model.security.CustomUserDetails;
import pers.project.api.common.util.ResultUtils;
import pers.project.api.common.util.bean.BeanCopierUtils;
import pers.project.api.security.mapper.UserProfileMapper;
import pers.project.api.security.model.entity.UserProfile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

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

    private final UserProfileMapper userProfileMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        // 返回登录用户信息
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        LoginUserDTO loginUserDTO = new LoginUserDTO();
        BeanCopierUtils.copy(userDetails, loginUserDTO);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(JSON.toJSONString(ResultUtils.success(loginUserDTO)));
        // 更新用户登录信息
        LambdaUpdateChainWrapper<UserProfile> updateWrapper
                = new LambdaUpdateChainWrapper<>(userProfileMapper);
        updateWrapper.set(UserProfile::getIpAddress, userDetails.getIpAddress())
                .set(UserProfile::getIpOrigin, userDetails.getIpOrigin())
                .set(UserProfile::getLastLoginTime, LocalDateTime.now())
                .eq(UserProfile::getId, userDetails.getProfileId());
        updateWrapper.update();
    }

}
