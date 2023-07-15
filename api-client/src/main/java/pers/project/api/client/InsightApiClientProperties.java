package pers.project.api.client;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Insight API 客户端配置属性
 *
 * @author Luo Fei
 * @date 2023/07/14
 */
@ConfigurationProperties(prefix = "insight.api.client")
public class InsightApiClientProperties {

    /**
     * 密钥 ID
     */
    private String secretId;

    /**
     * 密钥值
     */
    private String secretKey;

    /**
     * 连接超时时间（单位：毫秒）
     */
    private long connectTimeout;

    public String getSecretId() {
        return secretId;
    }

    public void setSecretId(String secretId) {
        this.secretId = secretId;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public long getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(long connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

}
