package pers.project.api.security.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import pers.project.api.common.validation.constraint.ContainedIn;

/**
 * 验证码检查 DTO
 * <p>
 * 可作为其他 DTO 的字段。
 *
 * @author Luo Fei
 * @date 2023/04/20
 */
@Data
public class VerificationCodeCheckDTO {

    /**
     * 手机号码（中国大陆）
     */
    @Pattern(regexp = "^\\+861[3-9]\\d{9}$")
    private String phoneNumber;

    /**
     * 邮箱号
     */
    @Email
    private String emailAddress;

    /**
     * 验证策略（手机或邮箱）
     */
    @NotNull
    @ContainedIn(values = {"PHONE", "EMAIL"})
    private String strategy;

    /**
     * 验证码
     * <p>
     * 6 位纯数字。
     */
    @NotNull
    @Pattern(regexp = "^\\d{6}$")
    private String verificationCode;

}
