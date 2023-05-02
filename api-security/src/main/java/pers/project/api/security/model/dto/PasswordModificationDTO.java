package pers.project.api.security.model.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import pers.project.api.common.validation.constraint.SnowflakeId;

/**
 * 密码修改 DTO
 *
 * @author Luo Fei
 * @date 2023/04/30
 */
@Data
public class PasswordModificationDTO {

    /**
     * 账户 ID
     */
    @SnowflakeId
    private String accountId;

    /**
     * 原来的密码
     * <p>
     * 8 至 25 个字符，且不能为纯数字。
     */
    @Pattern(regexp = "^(?=.*\\d)(?=.*\\D).{8,25}$")
    private String originalPassword;

    /**
     * 新密码
     * <p>
     * 8 至 25 个字符，且不能为纯数字。
     */
    @NotNull
    @Pattern(regexp = "^(?=.*\\d)(?=.*\\D).{8,25}$")
    private String newPassword;

    /**
     * 验证码检查 DTO
     */
    @Valid
    private VerificationCodeCheckDTO codeCheckDTO;

}
