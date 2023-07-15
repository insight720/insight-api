package pers.project.api.client;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.time.Duration;

/**
 * Insight API 客户端自动配置类
 * <p>
 * 可以单独创建一个 stater 依赖，和 api-client 解耦，
 * 这样 api-client 就不依赖于 Spring 框架。
 * <p>
 * 如果删除自动配置相关内容，api-client 当前仅依赖于 Spring 的工具类。
 *
 * @author Luo Fei
 * @date 2023/07/14
 */
@AutoConfiguration
@EnableConfigurationProperties(InsightApiClientProperties.class)
public class InsightApiClientAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "insight.api.client", name = {"secretId", "secretKey"})
    public InsightApiClient insightApiClient(InsightApiClientProperties properties) {
        return InsightApiClient.newBuilder()
                .secretId(properties.getSecretId())
                .secretKey(properties.getSecretKey())
                .connectTimeout(Duration.ofMillis(properties.getConnectTimeout()))
                .build();
    }

}
