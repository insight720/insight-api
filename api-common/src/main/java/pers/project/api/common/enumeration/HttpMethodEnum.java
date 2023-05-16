package pers.project.api.common.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.springframework.http.HttpMethod;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;

/**
 * HTTP 请求方法枚举
 *
 * @author Luo Fei
 * @date 2023/05/04
 */
@Getter
@Accessors(fluent = true)
@AllArgsConstructor
public enum HttpMethodEnum {

    GET(0, "GET", "获取资源", HttpMethod.GET),
    HEAD(1, "HEAD", "获取资源头部信息", HttpMethod.HEAD),
    POST(2, "POST", "提交数据", HttpMethod.POST),
    PUT(3, "PUT", "修改资源", HttpMethod.PUT),
    DELETE(4, "DELETE", "删除资源", HttpMethod.DELETE),
    OPTIONS(5, "OPTIONS", "查询服务器支持的请求方法", HttpMethod.OPTIONS),
    TRACE(6, "TRACE", "追踪请求-响应的传输路径", HttpMethod.TRACE),
    PATCH(7, "PATCH", "更新资源局部内容", HttpMethod.PATCH);

    /**
     * 数据库中存储的值
     */
    private final Integer mappedValue;

    /**
     * 请求方法名
     */
    private final String methodName;

    /**
     * 请求方法描述
     */
    private final String description;

    /**
     * Spring 的 {@code HttpMethod} 类
     *
     * @see HttpMethod
     */
    private final HttpMethod httpMethod;

    /**
     * {@code value} 到枚举常量的映射
     */
    private static final Map<Integer, HttpMethodEnum> MAPPED_VALUE_ENUM_MAP;

    static {
        HttpMethodEnum[] statusEnums = HttpMethodEnum.values();
        MAPPED_VALUE_ENUM_MAP = new HashMap<>(statusEnums.length);
        for (HttpMethodEnum statusEnum : statusEnums) {
            MAPPED_VALUE_ENUM_MAP.put(statusEnum.mappedValue, statusEnum);
        }
    }

    /**
     * 通过 {@code mappedValue} 获取对应的枚举常量
     *
     * @param mappedValue 数据库中存储的值
     * @return 对应的枚举常量
     * @throws IllegalArgumentException 如果 {@code mappedValue} 不存在对应的枚举常量
     */
    public static HttpMethodEnum getEnumByMappedValue(Integer mappedValue) {
        HttpMethodEnum methodEnum = MAPPED_VALUE_ENUM_MAP.get(mappedValue);
        Assert.notNull(methodEnum, () -> """
                No enum constant %s with mappedValue %d
                """.formatted(HttpMethodEnum.class.getName(), mappedValue));
        return methodEnum;
    }

}
