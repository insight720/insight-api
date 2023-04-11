package pers.project.api.security.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 表【user_profile (用户资料) 】的数据 Entity
 *
 * @author Luo Fei
 * @date 2023/03/20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "user_profile")
public class UserProfile {

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String id;

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
    private String ipOrigin;

    /**
     * 上次登录时间
     */
    private LocalDateTime lastLoginTime;

    /**
     * 是否删除（1 表示删除，0 表示未删除）
     */
    @TableLogic
    private Integer isDeleted;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

}