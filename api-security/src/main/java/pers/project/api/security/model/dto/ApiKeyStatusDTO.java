package pers.project.api.security.model.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import pers.project.api.common.validation.constraint.ContainedIn;
import pers.project.api.common.validation.constraint.SnowflakeId;
import pers.project.api.security.enumeration.AccountStatusEnum;

/**
 * API 密钥状态 DTO
 *
 * @author Luo Fei
 * @date 2023/04/06
 */
@Data
public class ApiKeyStatusDTO {

    /**
     * 账户主键
     */
    @SnowflakeId
    private String accountId;

    /**
     * 原来的状态
     *
     * @see AccountStatusEnum#statusCode()
     */
    @NotNull
    @ContainedIn(values = {"NORMAL_KEY_AVAILABLE", "NORMAL_KEY_UNAVAILABLE"})
    private String originalStatus;

    /**
     * 目标状态
     *
     * @see AccountStatusEnum#statusCode()
     */
    @NotNull
    @ContainedIn(values = {"NORMAL_KEY_AVAILABLE", "NORMAL_KEY_UNAVAILABLE"})
    private String targetStatus;

    /**
     * 验证码检查 DTO
     */
    @Valid
    private VerificationCodeCheckDTO codeCheckDTO;

}
