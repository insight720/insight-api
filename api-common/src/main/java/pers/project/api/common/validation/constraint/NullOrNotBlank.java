package pers.project.api.common.validation.constraint;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotBlank;
import pers.project.api.common.validation.validator.NullOrNotBlankValidator;

import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


/**
 * {@code NullOrNotBlank} 校验注解
 * <p>
 * 被注解的 {@link CharSequence} 可以为 {@code null}。
 * <p>
 * 如果不为 {@code null}，那么它的字符串表示至少包含一个非空格字符。
 * <p>
 * 根据 Jakarta Bean Validation specification，将 {@code null} 校验和其他校验分开是一种很好的做法，但 {@link NotBlank} 没有这么做。
 *
 * @author Luo Fei
 * @date 2023/04/07
 * @see Character#isWhitespace(int)
 * @see <a href="https://beanvalidation.org/2.0/spec/#constraintsdefinitionimplementation-validationimplementation">
 * Jakarta Bean Validation specification</a>
 */
@Documented
@Constraint(validatedBy = {NullOrNotBlankValidator.class})
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
@Repeatable(NullOrNotBlank.List.class)
public @interface NullOrNotBlank {

    String message() default "{pers.project.api.common.validation.constraint.NullOrNotBlank.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * 对同一元素定义多个 {@code @NullOrNotBlank} 约束。
     *
     * @see NullOrNotBlank
     */
    @Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
    @Retention(RUNTIME)
    @Documented
    @interface List {
        NullOrNotBlank[] value();
    }

}
