package pers.project.api.common.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 用户接口计数用法枚举
 *
 * @author Luo Fei
 * @date 2023/07/05
 */
@Getter
@RequiredArgsConstructor
@Accessors(fluent = true)
public enum UserQuantityUsageStatusEnum {

    NEW(0, "新建（如果之前没有使用过，将在在订单超时取消后删除）");

    /**
     * 数据库中存储的值
     */
    private final Integer storedValue;

    /**
     * 状态描述
     */
    private final String description;

}
