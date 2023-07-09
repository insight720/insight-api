package pers.project.api.common.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * 接口计数用法订单状态枚举
 *
 * @author Luo Fei
 * @date 2023/07/08
 */
@Getter
@AllArgsConstructor
@Accessors(fluent = true)
public enum QuantityUsageOrderStatusEnum {

    NEW(0, "订单新建"),

    SUCCESS(1, "下单成功"),

    STOCK_SHORTAGE(2, "存量不足"),

    TIMEOUT_CANCELLATION(3, "超时取消"),

    USER_CANCELLATION(4, "用户取消"),

    CONFIRMATION(5, "订单确认");;

    /**
     * 数据库中存储的值
     */
    private final Integer storedValue;

    /**
     * 状态描述
     */
    private final String description;

}
