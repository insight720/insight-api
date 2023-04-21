package pers.project.api.security.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 手机或邮箱登录的 DTO
 *
 * @author Luo Fei
 * @date 2023/04/19
 */
@Data
public class PhoneOrEmailLoginDTO {

    /**
     * 是否勾选了记住我
     * <p>
     * 如果用户勾选了记住我，该字段的值为 "true"，否则为 "false"。
     */
    @NotNull
    @Pattern(regexp = "^(true|false)$")
    private String rememberMe;

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
    @NotNull
    @Pattern(regexp = "^\\d{6}$")
    private String verificationCode;
    // endregion

}
