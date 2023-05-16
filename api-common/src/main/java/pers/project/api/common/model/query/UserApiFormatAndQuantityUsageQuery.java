package pers.project.api.common.model.query;

import lombok.Data;
import pers.project.api.common.validation.constraint.SnowflakeId;

/**
 * 账户主键和 API 摘要主键 Query
 *
 * @author Luo Fei
 * @date 2023/05/12
 */
@Data
public class UserApiFormatAndQuantityUsageQuery {

    /**
     * 账户主键
     */
    @SnowflakeId
    private String accountId;

    /**
     * API 摘要主键
     */
    @SnowflakeId
    private String digestId;

}
