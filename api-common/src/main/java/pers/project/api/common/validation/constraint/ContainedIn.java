package pers.project.api.common.validation.constraint;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import pers.project.api.common.validation.validator.containedin.AbstractContainedInValidator;
import pers.project.api.common.validation.validator.containedin.ContainedInValidatorForCollection;
import pers.project.api.common.validation.validator.containedin.ContainedInValidatorForObject;
import pers.project.api.common.validation.validator.containedin.ContainedInValidatorForObjectArray;

import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * {@code ContainedIn} 校验注解
 * <p>
 * 被注解的元素必须包含在 {@link ContainedIn#values()} 指定的值之中。
 * <p>
 * 支持的类型包括：
 * <ul>
 *     <li>{@code String}</li>
 *     <li>{@code Boolean Byte Character Short Integer Long Float Double}</li>
 * </ul>
 * 以上类型的 {@code Collection} 和 {@code Object[]} 也受支持。
 * <p>
 * 可以扩展 {@link AbstractContainedInValidator} 来支持更多的类型。
 * <p>
 * {@code null} 元素被视为有效。
 *
 * @author Luo Fei
 * @date 2023/04/27
 */
@Documented
@Constraint(validatedBy = {ContainedInValidatorForObject.class,
        ContainedInValidatorForObjectArray.class,
        ContainedInValidatorForCollection.class})
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
@Repeatable(ContainedIn.List.class)
public @interface ContainedIn {

    /**
     * 允许通过校验的值集合
     * <p>
     * 字符串会用常规方法（例如：{@link Long#parseLong}）
     * 转换为 {@link ContainedIn#element()} 指定的被校验类型。
     */
    String[] values() default {};

    /**
     * 被校验的元素类型
     * <p>
     * 作用于 {@code Collection} 或 {@code Object[]} 时，指的是其中元素的类型。
     */
    Class<?> element() default String.class;

    String message() default "{pers.project.api.common.validation.constraint.ContainedIn.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * 对同一元素定义多个 {@code @ContainedIn} 约束。
     *
     * @see ContainedIn
     */
    @Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
    @Retention(RUNTIME)
    @Documented
    @interface List {
        ContainedIn[] value();
    }

}
