package pers.project.api.common.model.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 登录用户 DTO
 *
 * @author Luo Fei
 * @date 2023/03/24
 */
@Data
public class LoginUserDTO {

    // region Same with UserAccount
    /**
     * 账号
     */
    private String username;
    /**
     * 邮箱
     */
    private String emailAddress;
    /**
     * 手机号
     */
    private String phoneNumber;
    /**
     * 权限
     */
    private String authority;
    /**
     * 密钥 ID
     */
    private String secretId;
    /**
     * 账号状态
     */
    private Integer accountStatus;
    // endregion

    /**
     * 用户资料主键
     */
    private String profileId;

    // region Same with UserProfile
    /**
     * 账户主键
     */
    private String accountId;
    /**
     * 头像
     */
    private String avatar;
    /**
     * 昵称
     */
    private String nickname;
    /**
     * 个人网站
     */
    private String website;
    /**
     * GitHub
     */
    private String github;
    /**
     * Gitee
     */
    private String gitee;
    /**
     * 个人简介
     */
    private String biography;
    /**
     * IP 地址
     */
    private String ipAddress;
    /**
     * IP 属地
     */
    private String ipLocation;
    /**
     * 上次登录时间
     */
    private LocalDateTime lastLoginTime;
    // endregion

}
