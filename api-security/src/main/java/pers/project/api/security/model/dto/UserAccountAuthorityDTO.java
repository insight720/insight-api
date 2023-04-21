package pers.project.api.security.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import pers.project.api.common.validation.constraint.SnowflakeId;

/**
 * 用户账户权限 DTO
 *
 * @author Luo Fei
 * @date 2023/04/06
 */
@Data
public class UserAccountAuthorityDTO {

    /**
     * 账户主键
     */
    @SnowflakeId
    private String accountId;

    /**
     * 账户权限
     */
    @NotBlank
    private String authority;

}
