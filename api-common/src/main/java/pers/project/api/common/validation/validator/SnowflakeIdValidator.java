package pers.project.api.common.validation.validator;

import com.baomidou.mybatisplus.core.incrementer.DefaultIdentifierGenerator;
import com.baomidou.mybatisplus.core.toolkit.Sequence;
import com.baomidou.mybatisplus.core.toolkit.SystemClock;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.constraintvalidation.SupportedValidationTarget;
import jakarta.validation.constraintvalidation.ValidationTarget;
import lombok.extern.slf4j.Slf4j;
import pers.project.api.common.validation.constraint.SnowflakeId;

/**
 * {@code SnowflakeId} 校验器
 * <p>
 * 默认情况下，被校验的 {@code String} 必须不为 {@code null} 且表示时间戳部分合法的雪花算法 ID。
 * <p>
 * 如果 {@link SnowflakeId#nullable()} 设置为 {@code true}，则被校验的 {@code String} 可以为 {@code null}。
 * <p>
 * 该校验器适用于校验 {@link DefaultIdentifierGenerator} 生成的 ID。
 *
 * @author Luo Fei
 * @date 2023/04/07
 */
@Slf4j
@SupportedValidationTarget(ValidationTarget.ANNOTATED_ELEMENT)
public class SnowflakeIdValidator implements ConstraintValidator<SnowflakeId, String> {

    private boolean nullable;

    @Override
    public void initialize(SnowflakeId snowflakeId) {
        nullable = snowflakeId.nullable();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // nullable 为 true，则 null 被视为有效，否则 null 被视为无效
        if (value == null) {
            return nullable;
        }
        // 雪花算法 ID 为 64 位二进制数，转换为十进制数是 19 位
        if (value.length() != 19) {
            return false;
        }
        // Long.parseLong(String, int) 允许以 + 或 - 开头的数字
        char firstChar = value.charAt(0);
        if (firstChar == '+' || firstChar == '-') {
            return false;
        }
        long id;
        try {
            // 64 位二进制数雪花算法 ID，第一位符号位恒为 0，其值不会超过 Long.MAX_VALUE
            id = Long.parseLong(value, 10);
        } catch (NumberFormatException e) {
            if (log.isDebugEnabled()) {
                // 调试目的
                log.debug("Illegal snowflake ID: " + value, e);
            }
            return false;
        }
        // 当前时间戳必须大于 ID 时间戳
        return SystemClock.now() > Sequence.parseIdTimestamp(id);
    }

}
