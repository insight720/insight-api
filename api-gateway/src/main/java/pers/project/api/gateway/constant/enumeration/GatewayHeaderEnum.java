package pers.project.api.gateway.constant.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 网关请求头枚举
 * <p>
 * 用户校验请求的请求头。
 *
 * @author Luo Fei
 * @date 2023/03/10
 */
@Getter
@RequiredArgsConstructor
public enum GatewayHeaderEnum {

    ACCESS_KEY("accessKey"),
    NONCE("nonce"),
    TIMESTAMP("timestamp"),
    SIGN("sign"),
    BODY("body");

    private final String name;

}
