package pers.project.api.common.model.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import pers.project.api.common.validation.constraint.SnowflakeId;

/**
 * 用户接口计数用法创建 DTO
 *
 * @author Luo Fei
 * @date 2023/06/05
 */
@Data
public class UserQuantityUsageCreationDTO {

    /**
     * 账户主键
     */
    @SnowflakeId
    private String accountId;

    /**
     * 接口摘要主键
     */
    @SnowflakeId
    private String digestId;

    /**
     * 订单数量
     */
    @NotNull
    @Positive
    private String orderQuantity;

}
