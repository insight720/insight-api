package pers.project.api.security.model.vo;

import lombok.Data;

/**
 * API 密钥对 VO
 * <pre>
 * accountKey 用于标识 API 调用者身份，可以简单类比为用户名。
 * accessKey 用于验证 API 调用者的身份，可以简单类比为密码。</pre>
 *
 * @author Luo Fei
 * @date 2023/04/04
 */
@Data
public class ApiKeyPairVO {

    private String accountKey;

    private String accessKey;

}
