package pers.project.api.security.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 账户权限 DTO
 *
 * @author Luo Fei
 * @date 2023/04/06
 */
@Data
public class AccountAuthorityDTO {

    /**
     * 账户主键
     */
    @NotBlank
    private String accountId;

    /**
     * 账户权限
     */
    @NotBlank
    private String authority;

}
