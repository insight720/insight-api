package pers.project.api.security.model.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import pers.project.api.common.validation.constraint.SnowflakeId;

/**
 * 接口计数用法订单取消 DTO
 *
 * @author Luo Fei
 * @date 2023/07/05
 */
@Data
public class QuantityUsageOrderCancellationDTO {

    /**
     * 验证码检查 DTO
     */
    @NotNull
    @Valid
    private VerificationCodeCheckDTO codeCheckDTO;

    /**
     * 订单号
     */
    @NotBlank
    private String orderSn;

    /**
     * 用户账户主键
     */
    @SnowflakeId
    private String accountId;

    /**
     * 接口摘要主键
     */
    @SnowflakeId
    private String digestId;

}
