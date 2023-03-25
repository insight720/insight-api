package pers.project.api.common.model.dto;

import lombok.Data;

/**
 * 登录用户 DTO
 *
 * @author Luo Fei
 * @date 2023/03/24
 */
@Data
public class LoginUserDTO {
    /**
     * 账号
     */
    private String username;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 手机号
     */
    private String phoneNumber;
    /**
     * 权限
     */
    private String authority;
    /**
     * 账号状态
     */
    private Integer accountStatus;
    // endregion

    /**
     * 用户资料主键
     */
    private Long profileId;

    // region Same with UserProfile
    /**
     * 账户主键
     */
    private Long accountId;
    /**
     * 昵称
     */
    private String nickname;
    /**
     * 头像
     */
    private String avatar;
    /**
     * 个人简介
     */
    private String biography;
    // endregion

}
