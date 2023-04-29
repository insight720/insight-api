package pers.project.api.common.validation.validator.containedin;

import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.constraintvalidation.SupportedValidationTarget;
import jakarta.validation.constraintvalidation.ValidationTarget;
import pers.project.api.common.validation.constraint.ContainedIn;

import java.util.Collection;

/**
 * {@link ContainedIn} 集合校验器
 * <p>
 * 被校验的 {@code Collection} 可以为 {@code null}。
 * <p>
 * 如果不为 {@code null}，那么它的所有元素必须包含在指定的 {@code Set} 中。
 *
 * @author Luo Fei
 * @date 2023/04/28
 */
// 根据 JLS，Collection<?> 是 Collection 的子类型，因此我们需要在此处显式引用 Collection，以支持将属性定义为 Collection（请参阅 HV-1551）
@SuppressWarnings("rawtypes")
@SupportedValidationTarget(ValidationTarget.ANNOTATED_ELEMENT)
public class ContainedInValidatorForCollection extends AbstractContainedInValidator<Collection> {

    @Override
    public boolean isValid(Collection collection, ConstraintValidatorContext context) {
        if (collection == null) {
            return true;
        }
        for (Object value : collection) {
            if (!validSet.contains(value)) {
                addMessageParameterValue(value.toString(), context);
                return false;
            }
        }
        return true;
    }

}
