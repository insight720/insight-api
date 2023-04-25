package pers.project.api.security.model.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
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
     * 3 至 25 个字符，不能仅含空白字符，且不包含敏感词。
     */
    @NotBlank
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

    /**
     * 验证码检查 DTO
     */
    @Valid
    private VerificationCodeCheckDTO codeCheckDTO;

}
