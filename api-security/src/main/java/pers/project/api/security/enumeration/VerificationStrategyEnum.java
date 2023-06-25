package pers.project.api.security.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 验证策略枚举
 *
 * @author Luo Fei
 * @date 2023/04/16
 */
@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
public enum VerificationStrategyEnum {

    PHONE("phoneVerificationStrategy", "手机短信验证码",
            "security:verification:code:phone:"),

    EMAIL("emailVerificationStrategy", "邮件验证码",
            "security:verification:code:email:");

    /**
     * 验证策略实现类的 Bean 名称
     * <p>
     * 默认为首字母小写的验证策略实现类名。
     */
    private final String beanName;

    /**
     * 验证码类型的描述
     */
    private final String description;

    /**
     * Redis String 结构的键名
     * <p>
     * 用于临时存储验证码。
     */
    private final String keyPrefix;

}
