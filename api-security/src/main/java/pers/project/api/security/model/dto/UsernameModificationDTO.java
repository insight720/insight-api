package pers.project.api.security.model.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import pers.project.api.common.validation.constraint.SensitiveWord;
import pers.project.api.common.validation.constraint.SnowflakeId;

/**
 * 账户名修改 DTO
 *
 * @author Luo Fei
 * @date 2023/04/29
 */
@Data
public class UsernameModificationDTO {

    /**
     * 账户 ID
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
     * 验证码检查 DTO
     */
    @NotNull
    @Valid
    private VerificationCodeCheckDTO codeCheckDTO;

}
