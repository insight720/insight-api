package pers.project.api.common.model.query;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Insight API 客户端的用户信息 Query
 * <p>
 * 可根据查询所需条件添加字段。
 *
 * @author Luo Fei
 * @date 2023/07/15
 */
@Data
public class ClientUserInfoQuery {

    /**
     * 密钥 ID
     */
    @NotBlank
    private String secretId;

}
