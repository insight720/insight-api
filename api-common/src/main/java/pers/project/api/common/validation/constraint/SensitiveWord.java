package pers.project.api.common.validation.constraint;

import com.github.houbb.sensitive.word.support.allow.WordAllows;
import com.github.houbb.sensitive.word.support.deny.WordDenys;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import pers.project.api.common.validation.validator.SensitiveWordValidator;

import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * {@code SensitiveWord} 校验注解
 * <p>
 * 被注解的 {@link CharSequence} 可以为 {@code null}。
 * <p>
 * 如果不为 {@code null}，那么它的字符串表示必须不包含敏感词。
 *
 * @author Luo Fei
 * @date 2023/04/08
 * @see SensitiveWordValidator
 * @see <a href="https://github.com/houbb/sensitive-word">sensitive-word</a>
 */
@Documented
@Constraint(validatedBy = {SensitiveWordValidator.class})
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
@Repeatable(SensitiveWord.List.class)
public @interface SensitiveWord {

    String message() default "{pers.project.api.common.validation.constraint.SensitiveWord.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * 是否启用严格的校验规则
     * <p>
     * 即是否使用 {@link  SensitiveWordValidator#STRICT_WORD_BS} 进行校验。
     * <p>
     * 注意：启用严格的校验规则，则 {@code SensitiveWord} 注解的其他设置项均无效。
     *
     * @return 如果启用，则设置为 {@code true}。
     */
    boolean strict() default false;

    /**
     * 设置允许的词汇
     * <p>
     * 如果 {@link SensitiveWord#useSystemWordAllows()} 设置为 true，
     * 还会允许 {@link WordAllows#defaults()} 添加的默认词汇。
     *
     * @return 允许的词汇
     */
    String[] wordAllows() default {};

    /**
     * 设置禁止的词汇
     * <p>
     * 如果 {@link SensitiveWord#useSystemWordDenys()} 设置为 true，
     * 还会禁止 {@link WordDenys#defaults()} 添加的默认词汇。
     *
     * @return 禁止的词汇
     */
    String[] wordDenys() default {};

    /**
     * 是否使用系统默认允许的词汇
     * <p>
     * 即允许 {@link WordAllows#defaults()} 添加的默认词汇。
     *
     * @return 如果使用，则设置为 {@code true}。
     */
    boolean useSystemWordAllows() default true;

    /**
     * 是否使用系统默认禁止的词汇
     * <p>
     * 即禁止 {@link WordDenys#defaults()} 添加的默认词汇。
     *
     * @return 如果使用，则设置为 {@code true}。
     */
    boolean useSystemWordDenys() default true;

    /**
     * 是否忽略大小写
     *
     * @return 如果忽略，则设置为 {@code true}。
     */
    boolean ignoreCase() default true;

    /**
     * 是否忽略半角圆角
     *
     * @return 如果忽略，则设置为 {@code true}。
     */
    boolean ignoreWidth() default true;

    /**
     * 是否忽略数字的写法
     *
     * @return 如果忽略，则设置为 {@code true}。
     */
    boolean ignoreNumStyle() default true;

    /**
     * 是否忽略中文的书写格式
     *
     * @return 如果忽略，则设置为 {@code true}。
     */
    boolean ignoreChineseStyle() default true;

    /**
     * 是否忽略英文的书写格式
     *
     * @return 如果忽略，则设置为 {@code true}。
     */
    boolean ignoreEnglishStyle() default true;

    /**
     * 是否忽略重复词
     *
     * @return 如果忽略，则设置为 {@code true}。
     */
    boolean ignoreRepeat() default true;

    /**
     * 是否启用数字检测
     *
     * @return 如果启用，则设置为 {@code true}。
     */
    boolean enableUrlCheck() default false;

    /**
     * 是否启用邮箱检测
     *
     * @return 如果启用，则设置为 {@code true}。
     */
    boolean enableEmailCheck() default false;

    /**
     * 是否启用数字检测
     *
     * @return 如果启用，则设置为 {@code true}。
     */
    boolean enableNumCheck() default false;

    /**
     * 数字检测长度
     * <p>
     * 如果 {@link SensitiveWord#enableNumCheck()} 为默认值 {@code false}，
     * 该设置项无效。
     *
     * @return 数字检测长度（默认为 8）
     */
    int numCheckLen() default 8;

    /**
     * 对同一元素定义多个 {@code @SensitiveWord} 约束。
     *
     * @see SensitiveWord
     */
    @Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
    @Retention(RUNTIME)
    @Documented
    @interface List {
        SensitiveWord[] value();
    }

}
