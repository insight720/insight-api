package pers.project.api.common.model.query;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * 用户管理分页 Query
 *
 * @author Luo Fei
 * @date 2023/05/20
 */
@Data
public class UserAdminPageQuery {

    /**
     * 每页显示条数
     */
    private Long size;

    /**
     * 当前页
     */
    private Long current;

    /**
     * 权限集合
     */
    private Set<String> authoritySet;

    /**
     * 账号状态集合
     */
    private Set<Integer> accountStatusSet;

    /**
     * 账户更新时间范围
     * <p>
     * [0] 起始时间
     * <p>
     * [1] 终止时间（包括）
     */
    private LocalDateTime[] accountUpdateTimeRange;

    /**
     * 资料更新时间范围
     * <p>
     * [0] 起始时间
     * <p>
     * [1] 终止时间（包括）
     */
    private LocalDateTime[] profileUpdateTimeRange;

    /**
     * 创建时间范围
     * <p>
     * [0] 起始时间
     * <p>
     * [1] 终止时间（包括）
     */
    private LocalDateTime[] createTimeRange;

    /**
     * 上次登录时间范围
     * <p>
     * [0] 起始时间
     * <p>
     * [1] 终止时间（包括）
     */
    private LocalDateTime[] lastLoginTimeRange;

    // region Same with UserAccountPO and UserProfilePO
    /**
     * 是否删除（1 表示删除，0 表示未删除）
     */
    private Integer isDeleted;
    // endregion

    // region Same with UserAccountPo
    /**
     * 账户名（模糊查询）
     */
    private String username;

    /**
     * 邮箱（模糊查询）
     */
    private String emailAddress;

    /**
     * 手机号（模糊查询）
     */
    private String phoneNumber;

    /**
     * 密钥 ID（模糊查询）
     */
    private String secretId;

    /**
     * 密钥值（模糊查询）
     */
    private String secretKey;
    // endregion

    // region Same with UserProfilePO
    /**
     * 账户主键
     */
    private String accountId;

    /**
     * 昵称（模糊查询）
     */
    private String nickname;

    /**
     * 个人网站（模糊查询）
     */
    private String website;

    /**
     * GitHub（模糊查询）
     */
    private String github;

    /**
     * Gitee（模糊查询）
     */
    private String gitee;

    /**
     * 个人简介（模糊查询）
     */
    private String biography;

    /**
     * IP 地址（模糊查询）
     */
    private String ipAddress;

    /**
     * IP 属地（模糊查询）
     */
    private String ipLocation;
    // endregion

}
