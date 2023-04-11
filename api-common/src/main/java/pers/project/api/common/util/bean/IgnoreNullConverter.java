package pers.project.api.common.util.bean;

import org.springframework.cglib.core.Converter;
import org.springframework.util.Assert;

import java.lang.reflect.Field;

import static org.springframework.util.ReflectionUtils.*;
import static org.springframework.util.StringUtils.uncapitalize;

/**
 * 不复制源 Bean 中 null 属性的转换器
 * <p>
 * 通常用于更新目标 Bean 的部分属性值。
 */
public class IgnoreNullConverter implements Converter {

    private static final int SETTER_NAME_PREFIX_LENGTH = 3;

    private final Object targetObject;

    /**
     * @param targetObject 目标 Bean
     */
    public IgnoreNullConverter(Object targetObject) {
        Assert.notNull(targetObject, "The targetObject must not be null");
        this.targetObject = targetObject;
    }

    /**
     * 将源 Bean 属性值转换为目标 Bean 属性值。
     *
     * @param value   源 Bean 属性值
     * @param target  目标 Bean 属性的 Class 对象
     * @param context 目标 Bean 属性的 Setter 方法名
     * @return 目标 Bean 属性值
     */
    @Override
    public Object convert(Object value, Class target, Object context) {
        if (value != null) {
            return value;
        }
        Assert.notNull(context, "The target's properties may be missing setters");
        String fieldName = uncapitalize(((String) context).substring(SETTER_NAME_PREFIX_LENGTH));
        Field targetField = findField(targetObject.getClass(), fieldName, target);
        Assert.notNull(targetField, () -> """
                The property with name %s and type %s could not be found on the target class
                """.formatted(fieldName, target));
        makeAccessible(targetField);
        return getField(targetField, targetObject);
    }

}