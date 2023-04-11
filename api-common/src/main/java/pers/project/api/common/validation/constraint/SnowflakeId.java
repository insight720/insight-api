package pers.project.api.common.validation.constraint;

import com.baomidou.mybatisplus.core.incrementer.DefaultIdentifierGenerator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import pers.project.api.common.validation.validator.SnowflakeIdValidator;

import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * {@code SnowflakeId} 校验注解
 * <p>
 * 默认情况下，被注解的 {@code String} 必须不为 {@code null} 且表示时间戳部分合法的雪花算法 ID。
 * <p>
 * 大多数情况下，ID 不能为 {@code null}。如果被校验的 {@code String} 可以为 {@code null}，可以将
 * {@link SnowflakeId#nullable()} 设置为 {@code true}。
 * <p>
 * 该注解适用于校验 {@link DefaultIdentifierGenerator} 生成的 ID。
 *
 * @author Luo Fei
 * @date 2023/04/07
 */
@Documented
@Constraint(validatedBy = {SnowflakeIdValidator.class})
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
@Repeatable(SnowflakeId.List.class)
public @interface SnowflakeId {

    /**
     * 被注解的 {@code String} 是否可以为 {@code null}
     *
     * @return 如果可以，则设置为 true。
     */
    boolean nullable() default false;

    String message() default "{pers.project.api.common.validation.constraint.SnowflakeId.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * 对同一元素定义多个 {@code @SnowflakeId} 约束。
     *
     * @see SnowflakeId
     */
    @Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
    @Retention(RUNTIME)
    @Documented
    @interface List {
        SnowflakeId[] value();
    }

}
