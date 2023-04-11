package pers.project.api.common.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.constraintvalidation.SupportedValidationTarget;
import jakarta.validation.constraintvalidation.ValidationTarget;
import pers.project.api.common.validation.constraint.NullOrNotBlank;

/**
 * {@link  NullOrNotBlank} 校验器
 * <p>
 * 被校验的 {@link CharSequence} 可以为 {@code null}。
 * <p>
 * 如果不为 {@code null}，那么它的字符串表示至少包含一个非空格字符。
 *
 * @author Luo Fei
 * @date 2023/04/07
 * @see Character#isWhitespace(int)
 */
@SupportedValidationTarget(ValidationTarget.ANNOTATED_ELEMENT)
public class NullOrNotBlankValidator implements ConstraintValidator<NullOrNotBlank, CharSequence> {

    @Override
    public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
        // null 被视为有效
        if (value == null) {
            return true;
        }
        // 至少包含一个非空格字符
        return !value.toString().isBlank();
    }

}
