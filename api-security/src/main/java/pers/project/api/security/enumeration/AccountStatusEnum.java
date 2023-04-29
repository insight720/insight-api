package pers.project.api.security.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;

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

    NORMAL_KEY_AVAILABLE(0, "正常 - 密钥可用"),

    NORMAL_KEY_UNAVAILABLE(1, "正常 - 密钥不可用"),

    DISABLED_KEY_AVAILABLE(2, "已禁用 - 密钥可用"),

    DISABLED_KEY_UNAVAILABLE(3, "已禁用 - 密钥不可用");

    /**
     * 账户状态码
     */
    private final Integer statusCode;

    /**
     * 账户状态描述
     */
    private final String description;

    /**
     * {@code statusCode} 到枚举常量的映射
     */
    private static final Map<Integer, AccountStatusEnum> STATUS_CODE_ENUM_MAP;

    static {
        AccountStatusEnum[] statusEnums = AccountStatusEnum.values();
        STATUS_CODE_ENUM_MAP = new HashMap<>(statusEnums.length);
        for (AccountStatusEnum statusEnum : statusEnums) {
            STATUS_CODE_ENUM_MAP.put(statusEnum.statusCode, statusEnum);
        }
    }

    /**
     * 通过 {@code statusCode} 获取对应的枚举常量
     *
     * @param statusCode 账户状态码
     * @return 对应的枚举常量
     * @throws IllegalArgumentException 如果 {@code statusCode} 不存在对应的枚举常量
     */
    public static AccountStatusEnum getEnumByStatusCode(Integer statusCode) {
        AccountStatusEnum statusEnum = STATUS_CODE_ENUM_MAP.get(statusCode);
        Assert.notNull(statusEnum, () -> """
                No enum constant %s with statusCode %d
                """.formatted(AccountStatusEnum.class.getName(), statusCode));
        return statusEnum;
    }

}

