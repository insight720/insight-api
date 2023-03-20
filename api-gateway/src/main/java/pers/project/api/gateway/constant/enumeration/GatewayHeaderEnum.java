package pers.project.api.gateway.constant.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 网关请求头枚举
 * <p>
 * 用户校验请求的请求头。
 *
 * @author Luo Fei
 * @version 2023/3/10
 */
@Getter
@AllArgsConstructor
public enum GatewayHeaderEnum {

    ACCESS_KEY("accessKey"),
    NONCE("nonce"),
    TIMESTAMP("timestamp"),
    SIGN("sign"),
    BODY("body");

    private final String name;

}
