package pers.project.api.common.model.dto;

import lombok.Data;

/**
 * Insight API 客户端的用户信息 DTO
 * <p>
 * 用户请求的验签和日志所需数据。
 *
 * @author Luo Fei
 * @date 2023/07/15
 */
@Data
public class ClientUserInfoDTO {

    /**
     * 用户帐户主键
     */
    private String accountId;

    /**
     * 密钥值
     */
    private String secretKey;

}
