package pers.project.api.security.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

/**
 * 账户状态 DTO
 *
 * @author Luo Fei
 * @date 2023/04/06
 */
@Data
public class AccountStatusDTO {

    /**
     * 账户主键
     */
    @NotBlank
    private String accountId;

    /**
     * 账户状态码
     */
    @NotNull
    @Range
    private Integer statusCode;

}
