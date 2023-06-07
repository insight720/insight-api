package pers.project.api.security.model.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * API 创建者 VO
 *
 * @author Luo Fei
 * @date 2023/06/06
 */
@Data
public class ApiCreatorVO {

    /**
     * 用户账户更新时间
     */
    private LocalDateTime accountUpdateTime;

    /**
     * 用户资料更新时间
     */
    private LocalDateTime profileUpdateTime;

    /**
     * 权限集合
     */
    private Set<String> authoritySet;

    // region Same with UserAccountPO and UserProfilePO
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    // endregion

    // region From UserAccountPO
    /**
     * 账户名
     */
    private String username;

    /**
     * 邮箱
     */
    private String emailAddress;

    /**
     * 账号状态
     */
    private Integer accountStatus;
    // endregion

    // region Same with UserProfilePO
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
     * IP 属地
     */
    private String ipLocation;

    /**
     * 上次登录时间
     */
    private LocalDateTime lastLoginTime;
    // endregion

}
