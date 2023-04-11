package pers.project.api.security.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pers.project.api.common.exception.BusinessException;
import pers.project.api.common.model.security.CustomUserDetails;
import pers.project.api.common.util.bean.BeanCopierUtils;
import pers.project.api.security.execption.UploadException;
import pers.project.api.security.mapper.UserProfileMapper;
import pers.project.api.security.model.entity.UserProfile;
import pers.project.api.security.model.vo.UserProfileSettingVO;
import pers.project.api.security.service.CustomUserDetailsService;
import pers.project.api.security.service.UserProfileService;
import pers.project.api.security.strategy.context.UploadContext;

import static pers.project.api.common.constant.enumeration.ErrorEnum.SERVER_ERROR;
import static pers.project.api.common.constant.enumeration.ErrorEnum.UPLOAD_ERROR;

/**
 * 针对表【user_profile (用户资料) 】的数据库操作 Service 实现
 *
 * @author Luo Fei
 * @date 2023/03/20
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl extends ServiceImpl<UserProfileMapper, UserProfile> implements UserProfileService {

    private static final String AVATAR_DIRECTORY_URI = "profile/avatar/";

    private final CustomUserDetailsService userDetailsService;

    @Override
    public String uploadAvatar(MultipartFile file) {
        // TODO: 2023/4/7 校验文件大小
        String avatarUrl;
        try {
            avatarUrl = UploadContext.executeStrategy(file, AVATAR_DIRECTORY_URI);
        } catch (UploadException e) {
            if (log.isWarnEnabled()) {
                String message = "Avatar upload failed, filename: %s, contentType: %s, size: %d"
                        .formatted(file.getOriginalFilename(), file.getContentType(), file.getSize());
                log.warn(message, e);
            }
            throw new BusinessException(UPLOAD_ERROR, "头像上传失败，请稍微后再试！");
        }
        return avatarUrl;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void updateUserProfile(UserProfileSettingVO profileSettingVO) {
        // 更新数据库用户资料
        UserProfile userProfile = new UserProfile();
        BeanCopierUtils.copy(profileSettingVO, userProfile);
        userProfile.setId(profileSettingVO.getProfileId());
        try {
            updateById(userProfile);
        } catch (Exception e) {
            // SQL 错误
            String message = "UserProfile setting failed, profile: " + profileSettingVO;
            log.error(message, e);
            throw new BusinessException(SERVER_ERROR, "服务器错误，请稍后再试！");
        }
        // 更新 Spring Security 上下文中的用户资料
        CustomUserDetails userDetails = userDetailsService.getLoginUserDetails();
        BeanCopierUtils.copyIgnoreNull(profileSettingVO, userDetails);
        userDetailsService.updateLoginUserDetails(userDetails);
    }

}




