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
import pers.project.api.common.util.transaction.TransactionUtils;
import pers.project.api.security.execption.UploadContextException;
import pers.project.api.security.mapper.UserProfileMapper;
import pers.project.api.security.model.dto.UserProfileSettingDTO;
import pers.project.api.security.model.po.UserProfilePO;
import pers.project.api.security.service.CustomUserDetailsService;
import pers.project.api.security.service.UserProfileService;
import pers.project.api.security.upload.UploadContext;

import static pers.project.api.common.enumeration.ErrorEnum.SERVER_ERROR;
import static pers.project.api.common.enumeration.ErrorEnum.UPLOAD_ERROR;
import static pers.project.api.security.enumeration.UploadFileEnum.AVATAR;

/**
 * 针对表【user_profile (用户资料) 】的数据库操作 Service 实现
 *
 * @author Luo Fei
 * @date 2023/03/20
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl extends ServiceImpl<UserProfileMapper, UserProfilePO> implements UserProfileService {

    private final UploadContext uploadContext;

    private final CustomUserDetailsService userDetailsService;

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void updateUserProfile(MultipartFile avatarFile, UserProfileSettingDTO profileSettingVO) {
        // 先明确需求，一个用户只存储一个头像
        String profileId = profileSettingVO.getProfileId();
        // 如果用户设置新头像，则尝试上传头像文件
        String newAvatar = null;
        boolean setNewAvatar = (avatarFile != null);
        if (setNewAvatar) {
            String originalAvatar = profileSettingVO.getOriginalAvatar();
            newAvatar = uploadNewAvatar(profileId, avatarFile, originalAvatar);
        }
        // 更新数据库用户资料
        UserProfilePO userProfilePO = new UserProfilePO();
        BeanCopierUtils.copy(profileSettingVO, userProfilePO);
        userProfilePO.setId(profileId);
        if (setNewAvatar) {
            userProfilePO.setAvatar(newAvatar);
        }
        try {
            updateById(userProfilePO);
        } catch (Exception e) {
            // SQL 错误
            String message = "UserProfile setting failed, profile: " + profileSettingVO;
            log.error(message, e);
            throw new BusinessException(SERVER_ERROR, "服务器错误，请稍后再试！");
        }
        // 更新 Spring Security 上下文中的用户资料
        CustomUserDetails userDetails = userDetailsService.getLoginUserDetails();
        // 不复制 null 值
        BeanCopierUtils.copyIgnoreNull(profileSettingVO, userDetails);
        if (setNewAvatar) {
            userDetails.setAvatar(newAvatar);
        }
        userDetailsService.updateLoginUserDetails(userDetails);
    }

    /**
     * 上传新头像
     *
     * @param profileId         用户资料 ID
     * @param avatarFile        头像文件
     * @param originalAvatarUrl 原来头像的 URL
     * @return 新上传头像的 URL
     */
    private String uploadNewAvatar(String profileId, MultipartFile avatarFile, String originalAvatarUrl) {
        // 上传新头像
        String newAvatarUrl;
        try {
            newAvatarUrl = uploadContext.upload(profileId, avatarFile, AVATAR);
        } catch (UploadContextException e) {
            if (log.isWarnEnabled()) {
                String filename = avatarFile.getOriginalFilename();
                String contentType = avatarFile.getContentType();
                long size = avatarFile.getSize();
                log.warn("""
                        Avatar upload failed, profileId: %s, \
                        filename: %s, contentType: %s, size in bytes: %d
                        """.formatted(profileId, filename, contentType, size), e);
            }
            throw new BusinessException(UPLOAD_ERROR, "头像上传失败，请稍微后再试！");
        }
        // 如果新头像和原头像相同，事务提交或回滚都不必删除头像
        if (newAvatarUrl.equals(originalAvatarUrl)) {
            return newAvatarUrl;
        }
        // 如果事务提交，则删除不再使用的原头像
        TransactionUtils.ifCommittedAfterCompletion(() -> {
            // 此处抛出异常不会导致回滚，也不会传播给调用者
            try {
                uploadContext.delete(originalAvatarUrl, AVATAR);
            } catch (UploadContextException e) {
                if (log.isWarnEnabled()) {
                    log.warn("""
                            Avatar deletion failed, profileId: %s, newAvatarUrl: %s
                            """.formatted(profileId, newAvatarUrl), e);
                }
            }
        });
        // 如果事务回滚，则删除已上传但未使用的新头像
        TransactionUtils.ifRolledBackAfterCompletion(() -> {
            // 此处抛出异常不会传播给调用者
            try {
                uploadContext.delete(newAvatarUrl, AVATAR);
            } catch (UploadContextException e) {
                if (log.isWarnEnabled()) {
                    log.warn("""
                            New avatar deletion failed, profileId: %s, newAvatarUrl: %s
                            """.formatted(profileId, newAvatarUrl), e);
                }
            }
        });
        // 未知状态的事务不做处理
        return newAvatarUrl;
    }

}




