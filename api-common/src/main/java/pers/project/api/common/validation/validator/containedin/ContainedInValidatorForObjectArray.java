package pers.project.api.common.validation.validator.containedin;

import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.constraintvalidation.SupportedValidationTarget;
import jakarta.validation.constraintvalidation.ValidationTarget;
import org.hibernate.validator.internal.engine.constraintvalidation.AbstractConstraintValidatorManagerImpl;
import pers.project.api.common.validation.constraint.ContainedIn;

/**
 * {@link ContainedIn} 对象数组校验器
 * <p>
 * 被校验的 {@code Object[]} 可以为 {@code null}。
 * <p>
 * 如果不为 {@code null}，那么它的所有元素必须包含在指定的 {@code Set} 中。
 * <p>
 * 如果有类型更具体的校验器，这个校验器不会被使用。
 * <p>
 * {@link AbstractConstraintValidatorManagerImpl#resolveAssignableTypes} 方法会选择一个最合适的校验器。
 *
 * @author Luo Fei
 * @date 2023/04/28
 */
// Suppress warnings for JavaDoc
@SuppressWarnings("all")
@SupportedValidationTarget(ValidationTarget.ANNOTATED_ELEMENT)
public class ContainedInValidatorForObjectArray extends AbstractContainedInValidator<Object[]> {

    @Override
    public boolean isValid(Object[] objects, ConstraintValidatorContext context) {
        if (objects == null) {
            return true;
        }
        for (Object value : objects) {
            if (!validSet.contains(value)) {
                addMessageParameterValue(value.toString(), context);
                return false;
            }
        }
        return true;
    }

}
