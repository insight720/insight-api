package pers.project.api.common.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * 接口用法类型枚举
 *
 * @author Luo Fei
 * @date 2023/05/28
 */
@Getter
@AllArgsConstructor
@Accessors(fluent = true)
public enum UsageTypeEnum {

    QUANTITY_USAGE("QUANTITY", "计数用法");

    /**
     * 数据库中存储的值
     */
    private final String storedValue;

    /**
     * 接口用法描述
     */
    private final String description;

    /**
     * 根据存储的值获取对应的用法类型枚举。
     *
     * @param storedValue 存储的值
     * @return 对应的用法类型枚举
     * @throws IllegalArgumentException 如果存储的值无效或不存在对应的枚举值
     */
    public static UsageTypeEnum getByStoredValue(String storedValue) {
        UsageTypeEnum[] values = values();
        for (UsageTypeEnum value : values) {
            if (value.storedValue().equals(storedValue)) {
                return value;
            }
        }
        throw new IllegalArgumentException("Invalid or unsupported stored value.");
    }

}
