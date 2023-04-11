package pers.project.api.common.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.constraintvalidation.SupportedValidationTarget;
import jakarta.validation.constraintvalidation.ValidationTarget;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartFile;
import pers.project.api.common.validation.constraint.FileSpec;

import java.util.Arrays;
import java.util.List;

/**
 * {@code FileSpec} 校验器
 * <p>
 * 被校验的 {@code MultipartFile} 可以为 {@code null}。
 * <p>
 * 如果不为 {@code null}，那么它传输文件的大小和类型必须符合要求。
 *
 * @author Luo Fei
 * @date 2023/04/09
 */
@Slf4j
@SupportedValidationTarget(ValidationTarget.ANNOTATED_ELEMENT)
public class FileSpecValidator implements ConstraintValidator<FileSpec, MultipartFile> {

    /**
     * 消息参数名
     * <p>
     * 用于在 {@code ValidationMessages.properties} 文件设置的校验失败提示消息中获取消息参数值。
     */
    private static final String MESSAGE_PARAMETER_NAME = "spec";

    /**
     * 允许的最大文件大小（单位：字节）
     */
    private long maxByteSize;

    /**
     * 允许的最小文件大小（单位：字节）
     */
    private long minByteSize;

    /**
     * @see FileSpec#compatible()
     */
    boolean allowCompatible;

    /**
     * @see FileSpec#mediaTypes()
     */
    private List<MediaType> allowedMediaTypes;

    @Override
    // Suppress DataSize.of
    @SuppressWarnings("all")
    public void initialize(FileSpec fileSpec) {
        // 检查 maxSize 和 minSize
        long maxSize = fileSpec.maxSize();
        long minSize = fileSpec.minSize();
        Assert.isTrue(maxSize >= 0, () -> "Illegal negative maxSize: " + maxByteSize);
        Assert.isTrue(minSize >= 0, () -> "Illegal negative minSize: " + minByteSize);
        Assert.isTrue(maxSize >= minSize, () -> """
                Illegal maxSize and minSize: maxSize %d is less than minSize %d
                """.formatted(maxSize, minSize));
        // 数据单位转换为字节
        try {
            maxByteSize = DataSize.of(maxSize, fileSpec.maxSizeUnit()).toBytes();
            minByteSize = DataSize.of(minSize, fileSpec.minSizeUnit()).toBytes();
        } catch (ArithmeticException e) {
            throw new IllegalArgumentException("""
                    Illegal maxSize and minSize: \
                    The product of maxSize %d and minSize %d overflows long
                    """.formatted(maxSize, minSize), e);
        }
        // 设置允许的 MediaType
        allowCompatible = fileSpec.compatible();
        try {
            allowedMediaTypes = Arrays.stream(fileSpec.mediaTypes())
                    .distinct().map(MediaType::parseMediaType)
                    .toList();
        } catch (InvalidMediaTypeException e) {
            throw new IllegalArgumentException
                    ("Illegal allowedMediaTypes: "
                     + Arrays.toString(fileSpec.mediaTypes()), e);
        }
    }

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        // null 被视为有效
        if (file == null) {
            return true;
        }
        // 文件大小范围为 minByteSize 到 maxByteSize
        long byteSize = file.getSize();
        if (byteSize < minByteSize || byteSize > maxByteSize) {
            addMessageParameterValue("""
                    The file size is %d bytes and is not within the range of %d to %d bytes
                    """.formatted(byteSize, minByteSize, maxByteSize), context);
            return false;
        }
        // 不允许没有 Content-Type
        String contentType = file.getContentType();
        if (contentType == null) {
            addMessageParameterValue("Missing Content-Type", context);
            return false;
        }
        // Content-Type 解析为 MediaType
        MediaType fileMediaType;
        try {
            // 通常 Content-Type 只有一种类型（多种类型会抛出 InvalidMediaTypeException）
            fileMediaType = MediaType.parseMediaType(contentType);
        } catch (InvalidMediaTypeException e) {
            if (log.isDebugEnabled()) {
                // 调试目的
                log.debug("Illegal contentType: " + contentType, e);
            }
            addMessageParameterValue("""
                    Content-Type %s cannot be parsed
                    """.formatted(contentType), context);
            return false;
        }
        // 判断 allowedMediaTypes 是否包括或兼容 fileMediaType
        for (MediaType allowedMediaType : allowedMediaTypes) {
            if (allowCompatible && allowedMediaType.isCompatibleWith(fileMediaType)) {
                // 允许兼容的 fileMediaType 并且 allowedMediaTypes 兼容 fileMediaType
                return true;
            } else if (allowedMediaType.includes(fileMediaType)) {
                // 不允许兼容的 fileMediaType，则 allowedMediaTypes 必须包括 fileMediaType
                return true;
            }
        }
        addMessageParameterValue("""
                Content-Type %s is not allowed
                """.formatted(contentType), context);
        return false;
    }

    /**
     * 添加校验失败提示消息的参数值
     *
     * @param parameterValue 消息参数值
     * @param context        校验器上下文
     */
    private void addMessageParameterValue(String parameterValue, ConstraintValidatorContext context) {
        if (context instanceof HibernateConstraintValidatorContext hibernateContext) {
            hibernateContext.addMessageParameter(MESSAGE_PARAMETER_NAME, parameterValue);
        }
    }

}