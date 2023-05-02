package pers.project.api.security.model.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import pers.project.api.common.validation.constraint.SensitiveWord;
import pers.project.api.common.validation.constraint.SnowflakeId;

/**
 * 用户名和密码设置 DTO
 *
 * @author Luo Fei
 * @date 2023/04/30
 */
@Data
public class UsernameAndPasswordSettingDTO {

    /**
     * 账户主键
     */
    @SnowflakeId
    private String accountId;

    /**
     * 新账户名
     * <p>
     * 3 至 25 个字符，不能仅含空白字符，且不包含敏感词。
     */
    @NotBlank
    @Size(min = 3, max = 25)
    @SensitiveWord
    private String newUsername;

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
    @NotNull
    @Valid
    private VerificationCodeCheckDTO codeCheckDTO;

}
