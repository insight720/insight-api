package pers.project.api.security.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 账户权限枚举
 * <p>
 * 枚举常量名是 Spring Security 的 Authority 值。
 *
 * @author Luo Fei
 * @date 2023/04/26
 */
@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
public enum AcountAuthorityEnum {

    ROLE_USER("USER", "用户"),

    ROLE_TEST("TEST", "测试"),

    ROLE_ADMIN("ADMIN", "管理员"),

    ROLE_ANONYMOUS("ANONYMOUS", "匿名");

    /**
     * 角色
     * <p>
     * Spring Security 的 Role 值。
     */
    private final String role;

    /**
     * 描述
     */
    private final String description;

}
