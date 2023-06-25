package pers.project.api.security.config.context;


import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.region.Region;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pers.project.api.security.config.property.UploadContextProperties;

/**
 * 上传上下文配置类
 *
 * @author Luo Fei
 * @date 2023/03/28
 */
@Configuration
@EnableConfigurationProperties(UploadContextProperties.class)
public class UploadContextConfig {

    /**
     * COS 配置类
     */
    @Configuration
    @RequiredArgsConstructor
    @EnableConfigurationProperties(UploadContextProperties.CosProperties.class)
    public static class CosConfig {

        private final UploadContextProperties.CosProperties properties;

        /**
         * COS 客户端
         * <p>
         * 主账号密钥或永久密钥不建议在生产环境使用。
         *
         * @see <a href="https://cloud.tencent.com/document/product/436/10199">Java SDK 快速入门</a>
         */
        @Bean
        public COSClient cosClient() {
            COSCredentials cosCredentials = new BasicCOSCredentials
                    (properties.getSecretId(), properties.getSecretKey());
            Region region = new Region(properties.getRegion());
            ClientConfig clientConfig = new ClientConfig(region);
            return new COSClient(cosCredentials, clientConfig);
        }

    }

}
