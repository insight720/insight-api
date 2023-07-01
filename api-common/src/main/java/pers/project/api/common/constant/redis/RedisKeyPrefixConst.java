package pers.project.api.common.constant.redis;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Redis 键名称前缀常量
 * <p>
 * 键名称前缀命名规则参考：
 * <pre>
 * security:verification:phone_verification_code:
 * 业务域所属的项目:业务域:数据内容:
 * </pre>
 *
 * @author Luo Fei
 * @date 2023/06/27
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RedisKeyPrefixConst {

    /**
     * 手机验证码键名称前缀
     * <p>
     * 此名称前缀后还有一层目录结构：手机号。
     * <p>
     * 具体格式为：
     * <pre>
     * security:verification:phone_verification_code:{phoneNumber}
     * </pre>
     */
    public static final String PHONE_VERIFICATION_CODE_KEY_PREFIX
            = "security:verification:phone_verification_code:";

    /**
     * 邮箱验证码键名称前缀
     * <p>
     * 此名称前缀后还有一层目录结构：邮箱地址。
     * <p>
     * 具体格式为：
     * <pre>
     * security:verification:email_verification_code:{emailAddress}
     * </pre>
     */
    public static final String EMAIL_VERIFICATION_CODE_KEY_PREFIX
            = "security:verification:email_verification_code:";

    /**
     * 接口计数用法存量扣减消息 KEYS 的键名称前缀
     * <p>
     * 此名称前缀后还有一层目录结构：订单号。
     * <p>
     * 具体格式为：
     * <pre>
     * facade:quantity_usage:stock_deduction_message_keys:{orderSn}
     * </pre>
     */
    public static final String QUANTITY_USAGE_STOCK_DEDUCTION_MESSAGE_KEYS_KEY_PREFIX
            = "facade:quantity_usage:stock_deduction_message_keys:";

    /**
     * 接口计数用法订单状态更新消息 KEYS 的键名称前缀
     * <p>
     * 此名称前缀后还有一层目录结构：订单号。
     * <p>
     * 具体格式为：
     * <pre>
     * security:order:quantity_usage_order_status_update_message_keys:{orderSn}
     * </pre>
     */
    public static final String QUANTITY_USAGE_ORDER_STATUS_UPDATE_MESSAGE_KEYS_KEY_PREFIX
            = "security:order:quantity_usage_order_status_update_message_keys:";

}
