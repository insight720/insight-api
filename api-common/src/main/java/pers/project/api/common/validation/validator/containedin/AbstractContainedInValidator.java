package pers.project.api.common.validation.validator.containedin;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;
import org.springframework.util.Assert;
import pers.project.api.common.validation.constraint.ContainedIn;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.springframework.util.ClassUtils.isPrimitiveOrWrapper;
import static org.springframework.util.ClassUtils.resolvePrimitiveIfNecessary;
import static org.springframework.util.ReflectionUtils.findMethod;
import static org.springframework.util.ReflectionUtils.invokeMethod;

/**
 * {@link ContainedIn} 抽象校验器
 * <p>
 * 该抽象类定义了 {@code ContainedIn} 注解的校验功能由 {@code Set} 支持，
 * <p>
 * 并在 {@link AbstractContainedInValidator#initialize} 方法中为子类确定了被校验元素的类型。
 *
 * @author Luo Fei
 * @date 2023/04/28
 */
public abstract class AbstractContainedInValidator<T>
        implements ConstraintValidator<ContainedIn, T> {

    /**
     * 消息参数名
     * <p>
     * 用于在 {@code ValidationMessages.properties} 文件设置的校验失败提示消息中获取消息参数值。
     */
    protected static final String MESSAGE_PARAMETER_NAME = "value";

    protected static final Map<Class<?>, String> WRAPPER_PARSER_MAP = new HashMap<>(7);

    static {
        WRAPPER_PARSER_MAP.put(Boolean.class, "parseBoolean");
        WRAPPER_PARSER_MAP.put(Byte.class, "parseByte");
        WRAPPER_PARSER_MAP.put(Short.class, "parseShort");
        WRAPPER_PARSER_MAP.put(Integer.class, "parseInt");
        WRAPPER_PARSER_MAP.put(Long.class, "parseLong");
        WRAPPER_PARSER_MAP.put(Float.class, "parseFloat");
        WRAPPER_PARSER_MAP.put(Double.class, "parseDouble");
    }

    protected Set<Object> validSet;

    protected Class<?> element;

    @Override
    // Suppress warnings for validSet.add
    @SuppressWarnings("all")
    public void initialize(ContainedIn containedIn) {
        String[] values = containedIn.values();
        validSet = new HashSet<>(values.length);
        element = containedIn.element();
        // String 直接存储
        if (element == String.class) {
            for (String value : values) {
                validSet.add(value);
            }
            return;
        }
        // 基本数据类型只能转换为包装类存储
        boolean isChar = (element == char.class || element == Character.class);
        if (isChar) {
            for (String value : values) {
                Assert.isTrue(value.length() == 1, "The value should be a char");
                validSet.add(value.charAt(0));
            }
            return;
        }
        boolean isVoid = (element == void.class || element == Void.class);
        if (!isVoid && isPrimitiveOrWrapper(element)) {
            Class<?> wrapper = resolvePrimitiveIfNecessary(element);
            String parser = WRAPPER_PARSER_MAP.get(wrapper);
            Method method = findMethod(wrapper, parser, String.class);
            for (String value : values) {
                validSet.add(invokeMethod(method, null, value));
            }
            return;
        }
        // 不支持的类型
        throw new IllegalArgumentException("The element class %s is not supported".formatted(element));
    }

    /**
     * 添加校验失败提示消息的参数值
     *
     * @param parameterValue 消息参数值
     * @param context        校验器上下文
     */
    protected void addMessageParameterValue(String parameterValue, ConstraintValidatorContext context) {
        if (context instanceof HibernateConstraintValidatorContext hibernateContext) {
            hibernateContext.addMessageParameter(MESSAGE_PARAMETER_NAME, parameterValue);
        }
    }

}
