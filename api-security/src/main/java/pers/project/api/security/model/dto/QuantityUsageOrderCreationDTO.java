package pers.project.api.security.model.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.URL;
import pers.project.api.common.validation.constraint.ContainedIn;
import pers.project.api.common.validation.constraint.SnowflakeId;

import java.util.Set;

/**
 * 计数用法订单创建 DTO
 *
 * @author Luo Fei
 * @date 2023/06/03
 */
@Data
public class QuantityUsageOrderCreationDTO {

    /**
     * 验证码检查 DTO
     */
    @NotNull
    @Valid
    private VerificationCodeCheckDTO codeCheckDTO;

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

    /**
     * 请求方法集合
     */
    @Size(min = 1)
    @ContainedIn(values = {"GET", "HEAD", "POST", "PUT", "DELETE", "OPTIONS", "TRACE", "PATCH"})
    private Set<String> methodSet;

    /**
     * 接口用法类型集合
     */
    @Size(min = 1)
    @ContainedIn(values = {"QUANTITY"})
    private Set<String> usageTypeSet;

    /**
     * 订单锁定的调用次数
     */
    @NotNull
    private String orderQuantity;

    // region Same with ApiDigestPo
    /**
     * 接口名称
     */
    @NotBlank
    private String apiName;

    /**
     * 接口描述
     */
    @NotBlank
    private String description;

    /**
     * 接口地址
     */
    @NotBlank
    @URL
    private String url;
    // endregion

}
