package pers.project.api.gateway.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 请求头常量
 *
 * @author Luo Fei
 * @date 2023/3/10
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum RequestHeadersEnum {

    ACCESS_KEY("accessKey"),
    NONCE("nonce"),
    TIMESTAMP("timestamp"),
    SIGN("sign"),
    BODY("body");

    private String name;

}
