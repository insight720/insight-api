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
 * 接口计数用法订单创建 DTO
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
    @Size(min = 1, max = 8)
    @ContainedIn(values = {"GET", "HEAD", "POST", "PUT", "DELETE", "OPTIONS", "TRACE", "PATCH"})
    private Set<String> methodSet;
    // TODO: 2023/7/2 也许仅传递订单相关接口的用法类型
    /**
     * 接口用法类型集合
     */
    @Size(min = 1, max = 1)
    @ContainedIn(values = {"QUANTITY"})
    private Set<String> usageTypeSet;
// TODO: 2023/6/30 校验字符串数值
    /**
     * 订单锁定的调用次数
     */
    @NotNull
    private String orderQuantity;

    // region Same with ApiDigestPO
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
