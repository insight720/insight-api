package pers.project.api.security.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import pers.project.api.common.validation.constraint.ContainedIn;
import pers.project.api.common.validation.constraint.NullOrNotBlank;
import pers.project.api.common.validation.constraint.SnowflakeId;

/**
 * 用户接口测试 DTO
 *
 * @author Luo Fei
 * @date 2023/06/09
 */
@Data
public class UserApiTestDTO {

    /**
     * 账户主键
     */
    @SnowflakeId
    private String accountId;

    /**
     * 密钥 ID
     */
    @NotBlank
    private String secretId;

    /**
     * 接口摘要主键
     */
    @SnowflakeId
    private String digestId;

    /**
     * 请求方法
     */
    @NotNull
    @ContainedIn(values = {"GET", "HEAD", "POST", "PUT", "DELETE", "OPTIONS", "TRACE", "PATCH"})
    private String method;

    /**
     * 请求参数
     */
    @NullOrNotBlank
    private String requestParam;

    /**
     * 请求头
     */
    @NullOrNotBlank
    private String requestHeader;

    /**
     * 请求体
     */
    @NullOrNotBlank
    private String requestBody;

}
