package pers.project.api.security.model.dto;

import jakarta.validation.Valid;
import lombok.Data;
import pers.project.api.common.validation.constraint.SnowflakeId;

/**
 * 账户验证码检查 DTO
 *
 * @author Luo Fei
 * @date 2023/04/24
 */
@Data
public class AccountVerificationCodeCheckDTO {

    /**
     * 用户账户主键
     */
    @SnowflakeId
    private String accountId;

    /**
     * 验证码检查 DTO
     */
    @Valid
    private VerificationCodeCheckDTO codeCheckDTO;

}
