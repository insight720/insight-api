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

}
