package pers.project.api.common.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户管理 VO
 *
 * @author Luo Fei
 * @date 2023/05/20
 */
@Data
public class UserAdminVO {

    /**
     * 用户资料主键
     */
    private String profileId;

    /**
     * 账户更新时间
     */
    private LocalDateTime accountUpdateTime;

    /**
     * 资料更新时间
     */
    private LocalDateTime profileUpdateTime;

    // region Same with UserAccountPO and UserProfilePO
    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 是否删除（1 表示删除，0 表示未删除）
     */
    private Integer isDeleted;
    // endregion

    // region Same with UserAccountPo
    /**
     * 账户名
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
     * 密钥值
     */
    private String secretKey;

    /**
     * 账号状态
     */
    private Integer accountStatus;
    // endregion

    // region Same with UserProfilePO

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

}
