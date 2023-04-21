package pers.project.api.security.model.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.URL;
import pers.project.api.common.validation.constraint.NullOrNotBlank;
import pers.project.api.common.validation.constraint.SensitiveWord;
import pers.project.api.common.validation.constraint.SnowflakeId;

/**
 * 用户资料设置 VO
 *
 * @author Luo Fei
 * @date 2023/03/31
 */
@Data
public class UserProfileSettingDTO {

    // region Only same with CustomUserDetails
    /**
     * 用户资料主键
     */
    @SnowflakeId
    private String profileId;
    // endregion

    /**
     * 原来的头像
     */
    @NullOrNotBlank
    @URL
    private String originalAvatar;

    // region Same with UserProfile and CustomUserDetails
    /**
     * 昵称
     */
    @NullOrNotBlank
    @Size(min = 3, max = 25)
    @SensitiveWord
    private String nickname;

    /**
     * 个人网站
     */
    @NullOrNotBlank
    @URL
    private String website;

    /**
     * GitHub
     */
    @NullOrNotBlank
    @Pattern(regexp = "^https?://github\\.com/.*$")
    private String github;

    /**
     * Gitee
     */
    @NullOrNotBlank
    @Pattern(regexp = "^https?://gitee\\.com/.*$")
    private String gitee;

    /**
     * 个人简介
     */
    @NullOrNotBlank
    @SensitiveWord
    private String biography;
    // endregion

}
