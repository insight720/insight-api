package pers.project.api.security.model.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import pers.project.api.common.validation.constraint.ContainedIn;
import pers.project.api.common.validation.constraint.SnowflakeId;

import java.util.Set;

/**
 * 非管理权限 DTO
 *
 * @author Luo Fei
 * @date 2023/04/26
 */
@Data
public class NonAdminAuthorityDTO {

    /**
     * 账户主键
     */
    @SnowflakeId
    private String accountId;

    /**
     * 原来的的权限集合
     */
    @NotNull
    @Size(min = 1, max = 3)
    @ContainedIn(values = {"ROLE_USER", "ROLE_TEST", "ROLE_ADMIN"})
    private Set<String> originalAuthoritySet;

    /**
     * 权限集合
     */
    @NotNull
    @Size(min = 1, max = 2)
    @ContainedIn(values = {"ROLE_USER", "ROLE_TEST"})
    private Set<String> targetAuthoritySet;

}
