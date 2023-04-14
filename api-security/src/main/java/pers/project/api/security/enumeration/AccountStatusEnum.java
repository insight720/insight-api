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

    NORMAL(0, "正常"),

    NOT_AVAILABLE(1, "不可用");

    private final Integer statusCode;

    private final String description;

}
