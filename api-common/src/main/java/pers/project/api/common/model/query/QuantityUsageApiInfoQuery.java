package pers.project.api.common.model.query;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.URL;
import pers.project.api.common.validation.constraint.ContainedIn;
import pers.project.api.common.validation.constraint.SnowflakeId;

/**
 * Insight API 计数用法接口的信息 Query
 * <p>
 * 查询的数据用于判断对 Provider 的请求是否可用。
 *
 * @author Luo Fei
 * @date 2023/07/15
 */
@Data
public class QuantityUsageApiInfoQuery {

    /**
     * 用户账户主键
     */
    @SnowflakeId
    private String accountId;

    /**
     * 请求方法
     */
    @NotNull
    @ContainedIn(values = {"GET", "HEAD", "POST", "PUT", "DELETE", "OPTIONS", "TRACE", "PATCH"})
    private String method;

    /**
     * 原始 URL
     * <p>
     * 包含路径变量占位符，但不包含请求参数。
     */
    @NotBlank
    @URL
    private String originalUrl;

}
