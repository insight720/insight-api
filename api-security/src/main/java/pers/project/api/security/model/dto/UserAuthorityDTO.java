package pers.project.api.security.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import pers.project.api.common.validation.constraint.ContainedIn;
import pers.project.api.common.validation.constraint.SnowflakeId;

import java.util.Set;

/**
 * 用户账户权限 DTO
 *
 * @author Luo Fei
 * @date 2023/04/06
 */
@Data
public class UserAuthorityDTO {

    /**
     * 账户主键
     */
    @SnowflakeId
    private String accountId;

    /**
     * 账户权限
     */
    @NotBlank
    @ContainedIn(values = {"ROLE_USER", "ROLE_TEST"})
    private Set<String> authority;

}
