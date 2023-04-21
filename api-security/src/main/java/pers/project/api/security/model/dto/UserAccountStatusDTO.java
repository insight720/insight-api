package pers.project.api.security.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import pers.project.api.common.validation.constraint.SnowflakeId;

/**
 * 用户账户状态 DTO
 *
 * @author Luo Fei
 * @date 2023/04/06
 */
@Data
public class UserAccountStatusDTO {

    /**
     * 账户主键
     */
    @SnowflakeId
    private String accountId;

    /**
     * 账户状态码
     */
    @NotNull
    private Integer statusCode;
//  TODO: 2023/4/15 约束
}
