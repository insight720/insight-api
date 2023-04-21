package pers.project.api.security.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 账户状态枚举
 *
 * @author Luo Fei
 * @date 2023/04/06
 */
@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
public enum AccountStatusEnum {

    NORMAL_KEY_AVAILABLE(1, "正常 - 密钥可用"),

    NORMAL_KEY_UNAVAILABLE(2, "正常 - 密钥不可用"),

    DISABLED_KEY_AVAILABLE(3, "已禁用 - 密钥可用"),

    DISABLED_KEY_UNAVAILABLE(4, "已禁用 - 密钥不可用");

    /**
     * 账户状态码
     */
    private final Integer statusCode;

    /**
     * 账户状态描述
     */
    private final String description;

}

