package pers.project.api.common.validation.validator;

import com.github.houbb.sensitive.word.api.IWordAllow;
import com.github.houbb.sensitive.word.api.IWordDeny;
import com.github.houbb.sensitive.word.bs.SensitiveWordBs;
import com.github.houbb.sensitive.word.support.allow.WordAllows;
import com.github.houbb.sensitive.word.support.deny.WordDenys;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.constraintvalidation.SupportedValidationTarget;
import jakarta.validation.constraintvalidation.ValidationTarget;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;
import pers.project.api.common.validation.constraint.SensitiveWord;

import java.util.List;

/**
 * {@code SensitiveWord} 校验器
 * <p>
 * 被校验的 {@link CharSequence} 可以为 {@code null}。
 * <p>
 * 如果不为 {@code null}，那么它的字符串表示必须不包含敏感词。
 * <p>
 * 如果没有自定义配置，默认使用 {@link SensitiveWordValidator#LENIENT_WORD_BS} 进行校验。
 * <p>
 * 如果 {@link SensitiveWord#strict()} 设置为 {@code true}，则使用
 * {@link SensitiveWordValidator#STRICT_WORD_BS} 进行校验。
 * <p>
 * 如果 {@link SensitiveWord#strict()} 为默认值 {@code false}，但有其他
 * 自定义设置，则使用新的 {@link SensitiveWordBs} 进行校验。
 *
 * @author Luo Fei
 * @date 2023/04/08
 * @see <a href="https://github.com/houbb/sensitive-word">sensitive-word</a>
 */
@SupportedValidationTarget(ValidationTarget.ANNOTATED_ELEMENT)
public class SensitiveWordValidator implements ConstraintValidator<SensitiveWord, CharSequence> {

    /**
     * 消息参数名
     * <p>
     * 用于在 {@code ValidationMessages.properties} 文件设置的校验失败提示消息中获取消息参数值。
     */
    private static final String MESSAGE_PARAMETER_NAME = "words";

    /**
     * 宽松的校验规则
     * <p>
     * 访问权限修饰符为 {@code public}。
     * <p>
     * 程序可以在其他地方使用这一规则，
     * 还可以在运行时通过 {@link SensitiveWordBs#init()} 方法更改配置。
     */
    public static final SensitiveWordBs LENIENT_WORD_BS = SensitiveWordBs.newInstance()
            .ignoreCase(true)
            .ignoreWidth(true)
            .ignoreNumStyle(true)
            .ignoreChineseStyle(true)
            .ignoreEnglishStyle(true)
            .ignoreRepeat(true)
            .enableUrlCheck(false)
            .enableEmailCheck(false)
            .enableNumCheck(false)
            .wordAllow(WordAllows.system())
            .wordDeny(WordDenys.system())
            .init();

    /**
     * 严格的校验规则
     * <p>
     * 访问权限修饰符为 {@code public}。
     * <p>
     * 程序可以在其他地方使用这一规则，
     * 还可以在运行时通过 {@link SensitiveWordBs#init()} 方法更改配置。
     */
    public static final SensitiveWordBs STRICT_WORD_BS = SensitiveWordBs.newInstance()
            .ignoreCase(false)
            .ignoreWidth(false)
            .ignoreNumStyle(false)
            .ignoreChineseStyle(false)
            .ignoreEnglishStyle(false)
            .ignoreRepeat(false)
            .enableUrlCheck(true)
            .enableEmailCheck(true)
            .enableNumCheck(true)
            .numCheckLen(8)
            .wordAllow(WordAllows.system())
            .wordDeny(WordDenys.system())
            .init();

    /**
     * 自定义的校验规则
     * <p>
     * 注解会为每一个被校验的元素创建一个单独的 Validator，
     * 所以自定义校验规则在程序启动后就不可改变。
     */
    private SensitiveWordBs customWordBs = null;

    /**
     * 是否启用严格的校验规则
     * <p>
     * 即是否使用 {@link SensitiveWordValidator#STRICT_WORD_BS} 进行校验。
     */
    private boolean strict = false;

    @Override
    public void initialize(SensitiveWord word) {
        // 启用严格的校验规则无需初始化 customWordBs
        if (word.strict()) {
            strict = true;
            return;
        }
        // 不启用严格的校验规则，但启用数字校验，必须初始化 customWordBs
        if (word.enableNumCheck()) {
            int numCheckLen = word.numCheckLen();
            if (numCheckLen < 0) {
                // 数字校验长度不正确
                String msg = """
                        Number check is enabled, \
                        but the numCheckLen is set incorrectly to %d
                        """.formatted(numCheckLen);
                throw new IllegalArgumentException(msg);
            }
            initializeCustomWordBs(word);
            return;
        }
        // 检查其他设置项是否要求初始化 customWordBs
        boolean requireCustomWordBs =
                word.wordAllows().length != 0 ||
                word.wordDenys().length != 0 ||
                !word.useSystemWordAllows() ||
                !word.useSystemWordDenys() ||
                !word.ignoreCase() ||
                !word.ignoreWidth() ||
                !word.ignoreNumStyle() ||
                !word.ignoreChineseStyle() ||
                !word.ignoreEnglishStyle() ||
                !word.ignoreRepeat() ||
                word.enableUrlCheck() ||
                word.enableEmailCheck();
        if (requireCustomWordBs) {
            initializeCustomWordBs(word);
        }
    }

    @Override
    public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
        // null 被视为有效
        if (value == null) {
            return true;
        }
        SensitiveWordBs wordBs;
        if (strict) {
            // 严格的校验规则
            wordBs = STRICT_WORD_BS;
        } else if (customWordBs == null) {
            // 宽松的校验规则
            wordBs = LENIENT_WORD_BS;
        } else {
            // 自定义的校验规则
            wordBs = customWordBs;
        }
        boolean invalid = wordBs.contains(value.toString());
        if (invalid) {
            if (context instanceof HibernateConstraintValidatorContext hibernateContext) {
                // 在校验失败提示消息中添加找到的所有敏感词
                List<String> sensitiveWords = wordBs.findAll(value.toString());
                hibernateContext.addMessageParameter(MESSAGE_PARAMETER_NAME, sensitiveWords);
            }
        }
        return !invalid;
    }

    /**
     * 初始化 {@link SensitiveWordValidator#customWordBs}
     *
     * @param word {@link SensitiveWord} 注解
     */
    private void initializeCustomWordBs(SensitiveWord word) {
        // 获取允许的词汇
        IWordAllow iWordAllow;
        String[] customAllows = word.wordAllows();
        if (word.useSystemWordAllows()) {
            // 包括系统默认允许的词汇和自定义的词汇
            iWordAllow = WordAllows.chains(WordAllows.system(), () -> List.of(customAllows));
        } else {
            // 仅包括自定义的词汇
            iWordAllow = () -> List.of(customAllows);
        }
        // 获取禁止的词汇
        IWordDeny iWordDeny;
        String[] otherDenys = word.wordDenys();
        if (word.useSystemWordDenys()) {
            // 包括默认禁止的词汇和自定义的词汇
            iWordDeny = WordDenys.chains(WordDenys.system(), () -> List.of(otherDenys));
        } else {
            // 仅包括自定义的词汇
            iWordDeny = () -> List.of(otherDenys);
        }
        // 如果有自定义设置，就使用自定义设置，否则使用默认设置
        customWordBs = SensitiveWordBs.newInstance()
                .wordAllow(iWordAllow)
                .wordDeny(iWordDeny)
                .ignoreCase(word.ignoreCase())
                .ignoreWidth(word.ignoreWidth())
                .ignoreNumStyle(word.ignoreNumStyle())
                .ignoreChineseStyle(word.ignoreChineseStyle())
                .ignoreEnglishStyle(word.ignoreEnglishStyle())
                .ignoreRepeat(word.ignoreRepeat())
                .enableUrlCheck(word.enableUrlCheck())
                .enableEmailCheck(word.enableEmailCheck())
                .enableNumCheck(word.enableNumCheck())
                .numCheckLen(word.numCheckLen())
                .init();
    }

}
