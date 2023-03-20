package pers.project.api.sdk;

import lombok.Data;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import pers.project.api.sdk.client.TestClient;

/**
 * @author Luo Fei
 * @date 2023/1/21
 */
@Data
@AutoConfiguration
@ConfigurationProperties("api.client")
public class SdkConfig {

    private String accessKey;

    private String secretKey;

    @Bean
    public TestClient testClient() {
        return new TestClient(accessKey, secretKey);
    }

}
