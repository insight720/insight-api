package pers.project.api.security.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import pers.project.api.common.validation.constraint.SensitiveWord;

/**
 * 用户注册 DTO
 *
 * @author Luo Fei
 * @date 2023/03/22
 */
@Data
public class UserRegistryDTO {

    /**
     * 账户名
     * <p>
     * 3 至 25 个字符，不包含敏感词。
     */
    @NotNull
    @Size(min = 3, max = 25)
    @SensitiveWord
    private String username;

    /**
     * 密码
     * <p>
     * 8 至 25 个字符，且不能为纯数字。
     */
    @NotNull
    @Pattern(regexp = "^(?=.*\\d)(?=.*\\D).{8,25}$")
    private String password;

    /**
     * 确认密码
     */
    @NotNull
    private String confirmedPassword;

    // region Same with VerificationCodeCheckDTO
    /**
     * 邮箱号
     */
    @Email
    private String emailAddress;

    /**
     * 手机号码（中国国内）
     */
    @Pattern(regexp = "^\\+861[3-9]\\d{9}$")
    private String phoneNumber;

    /**
     * 验证策略（手机或邮箱）
     */
    @NotNull
    @Pattern(regexp = "^(PHONE|EMAIL)$")
    private String strategy;

    /**
     * 验证码
     * <p>
     * 6 位纯数字。
     */
    @Pattern(regexp = "^\\d{6}$")
    private String verificationCode;
    // endregion

}
