package pers.project.api.common.validation.constraint;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.MediaType;
import org.springframework.util.unit.DataSize;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import pers.project.api.common.validation.validator.FileSpecValidator;

import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * {@code FileSpecs} 校验注解
 * <p>
 * 被注解的 {@link MultipartFile} 可以为 {@code null}。
 * 如果不为 {@code null}，那么它传输文件的大小和类型必须符合要求。
 * <p>
 * 注意：此注解通常不用与 {@link NotNull} 一起使用，因为类似 {@link RequestPart#required()}
 * 的属性值为 {@code true} 时，{@link MultipartFile} 不可能是 null。
 *
 * @author Luo Fei
 * @date 2023/04/09
 */
@Documented
@Constraint(validatedBy = {FileSpecValidator.class})
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
@Repeatable(FileSpec.List.class)
public @interface FileSpec {

    String message() default "{pers.project.api.common.validation.constraint.FileSpec.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * 允许的最大文件大小
     * <p>
     * 默认为 {@link Long#MAX_VALUE} 字节，相当于不做限制。
     *
     * @see DataSize#parse(CharSequence)
     */
    String maxSize() default "9223372036854775807B";

    /**
     * 允许的最小文件大小
     * <p>
     * 默认为 1 字节，仅不允许空的 {@code MultipartFile}。
     *
     * @see DataSize#parse(CharSequence)
     */
    String minSize() default "1B";

    /**
     * 允许的文件类型
     * <p>
     * 默认为所有类型，使用 {@link MediaType} 中的常量进行设置。
     *
     * @see MediaType#parseMediaType(String)
     */
    String[] mediaTypes() default {MediaType.ALL_VALUE};

    /**
     * 是否允许相容的 {@code MediaType}
     * <p>
     * 默认为 {@code false}，会使用 {@link MediaType#includes(MediaType)}
     * 判断文件的 {@code MediaType} 是否被允许。
     * <p>
     * 如果设置为 {@code true}，则使用 {@link MediaType#isCompatibleWith(MediaType)}
     * 判断文件的 {@code MediaType} 是否被允许。
     */
    boolean compatible() default false;

    /**
     * 对同一元素定义多个 {@code @FileSpecs} 约束。
     *
     * @see FileSpec
     */
    @Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
    @Retention(RUNTIME)
    @Documented
    @interface List {
        FileSpec[] value();
    }

}
