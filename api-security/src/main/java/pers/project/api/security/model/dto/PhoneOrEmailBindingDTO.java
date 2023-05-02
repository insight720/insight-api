package pers.project.api.security.model.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import pers.project.api.common.validation.constraint.SnowflakeId;

/**
 * 手机或电子邮件绑定 DTO
 *
 * @author Luo Fei
 * @date 2023/05/02
 */
@Data
public class PhoneOrEmailBindingDTO {

    /**
     * 账户主键
     */
    @SnowflakeId
    private String accountId;

    /**
     * 验证码检查 DTO
     */
    @NotNull
    @Valid
    private VerificationCodeCheckDTO codeCheckDTO;

}
