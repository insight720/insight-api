package pers.project.api.security.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import pers.project.api.common.validation.constraint.ContainedIn;

/**
 * 验证码发送 DTO
 *
 * @author Luo Fei
 * @date 2023/04/16
 */
@Data
public class VerificationCodeSendingDTO {

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

}
