package pers.project.api.gateway.enumaration;

/**
 * 签名请求头枚举
 * <p>
 * 均添加 Insight- 前缀。
 * <p>
 * 与 {@code pers.project.api.client.enumaration.SignatureRequestHeaderEnum} 保持一致。
 *
 * @author Luo Fei
 * @date 2023/07/13
 */
public enum SignatureRequestHeaderEnum {

    USAGE_TYPE("Insight-Usage-Type"),

    ORIGINAL_URL("Insight-Original-Url"),

    SECRET_ID("Insight-Secret-Id"),

    TIMESTAMP("Insight-Timestamp"),

    NONCE("Insight-Nonce"),

    SIGN("Insight-Sign");

    private final String headerName;

    SignatureRequestHeaderEnum(String headerName) {
        this.headerName = headerName;
    }

    public String getHeaderName() {
        return headerName;
    }

}

