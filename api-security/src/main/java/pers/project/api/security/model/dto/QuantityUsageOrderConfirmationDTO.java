package pers.project.api.security.model.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import pers.project.api.common.validation.constraint.SnowflakeId;

/**
 * 接口计数用法订单确认 DTO
 *
 * @author Luo Fei
 * @date 2023/07/06
 */
@Data
public class QuantityUsageOrderConfirmationDTO {

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
     * 用户接口计数用法主键
     */
    @SnowflakeId
    private String usageId;

    /**
     * 确认的调用次数存量
     */
    @NotBlank
    private String quantity;

}
