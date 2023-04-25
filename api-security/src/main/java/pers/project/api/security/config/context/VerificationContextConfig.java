package pers.project.api.security.config.context;

import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.ses.v20201002.SesClient;
import com.tencentcloudapi.sms.v20210111.SmsClient;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pers.project.api.security.config.property.VerificationContextProperties;

/**
 * 验证上下文配置
 *
 * @author Luo Fei
 * @date 2023/04/16
 */
@Configuration
@EnableConfigurationProperties(VerificationContextProperties.class)
public class VerificationContextConfig {

    /**
     * 手机验证配置
     */
    @Configuration
    @RequiredArgsConstructor
    @EnableConfigurationProperties(VerificationContextProperties.PhoneProperties.class)
    public static class PhoneConfig {

        private final VerificationContextProperties.PhoneProperties properties;

        /**
         * 腾讯云 SMS 客户端
         *
         * @see <a href="https://cloud.tencent.com/document/product/382/43194">Java SDK</a>
         */
        @Bean
        public SmsClient smsClient() {
            Credential credential = new Credential
                    (properties.getSecretId(), properties.getSecretKey());
            return new SmsClient(credential, properties.getRegion());
        }

    }

    /**
     * 邮件验证配置
     */
    @Configuration
    @RequiredArgsConstructor
    @EnableConfigurationProperties(VerificationContextProperties.EmailProperties.class)
    public static class EmailConfig {

        private final VerificationContextProperties.EmailProperties properties;

        /**
         * 腾讯云 CMS 客户端
         *
         * @see <a href="https://cloud.tencent.com/document/api/1288/51034">
         * 发送邮件</a>
         */
        @Bean
        public SesClient sesClient() {
            Credential credential = new Credential
                    (properties.getSecretId(), properties.getSecretKey());
            return new SesClient(credential, properties.getRegion());
        }

    }

}
