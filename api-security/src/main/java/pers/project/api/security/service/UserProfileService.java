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
     * 上传头像
     *
     * @param file 头像文件
     * @return 头像 URL
     */
    String uploadAvatar(MultipartFile file);

    /**
     * 更新用户资料
     *
     * @param profileSettingVO 资料设置 VO
     */
    void updateUserProfile(UserProfileSettingVO profileSettingVO);

}
