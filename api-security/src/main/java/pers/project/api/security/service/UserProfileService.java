package pers.project.api.security.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;
import pers.project.api.security.model.entity.UserProfile;
import pers.project.api.security.model.vo.UserProfileSettingVO;

/**
 * 针对表【user_profile (用户资料) 】的数据库操作 Service
 *
 * @author Luo Fei
 * @date 2023/03/20
 */
public interface UserProfileService extends IService<UserProfile> {

    /**
     * 更新用户资料
     *
     * @param avatarFile       头像文件
     * @param profileSettingVO 资料设置 VO
     */
    void updateUserProfile(MultipartFile avatarFile, UserProfileSettingVO profileSettingVO);

}
