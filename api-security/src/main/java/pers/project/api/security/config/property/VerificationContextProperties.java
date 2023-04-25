package pers.project.api.security.config.property;

import com.tencentcloudapi.ses.v20201002.models.SendEmailRequest;
import com.tencentcloudapi.ses.v20201002.models.Template;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 验证上下文属性
 *
 * @author Luo Fei
 * @date 2023/04/16
 */
@Data
@ConfigurationProperties(prefix = "insight-api.verification")
public class VerificationContextProperties {

    /**
     * 手机验证属性
     *
     * @see <a href="https://cloud.tencent.com/document/product/382/43194">
     * Tencent Cloud Java SDK</a>
     */
    @Data
    @ConfigurationProperties(prefix = "insight-api.verification.phone")
    public static class PhoneProperties {

        // region Tencent Cloud
        /**
         * 开发者拥有的项目身份识别 ID（腾讯云）
         */
        private String secretId;

        /**
         * 开发者拥有的项目身份密钥（腾讯云）
         */
        private String secretKey;

        /**
         * 地域信息
         */
        private String region;

        /**
         * 短信应用 ID
         */

        private String smsSdkAppId;

        /**
         * 短信签名内容
         */
        private String signName;

        /**
         * 模板 ID
         */
        private String templateId;
        // endregion

        /**
         * 验证有效期（单位：分钟）
         */
        private int validityPeriod;

    }

    /**
     * 邮件验证属性
     */
    @Data
    @ConfigurationProperties(prefix = "insight-api.verification.email")
    public static class EmailProperties {

        // region Tencent Cloud
        /**
         * 开发者拥有的项目身份识别 ID（腾讯云）
         */
        private String secretId;

        /**
         * 开发者拥有的项目身份密钥（腾讯云）
         */
        private String secretKey;

        /**
         * 地域信息
         */
        private String region;

        /**
         * 发信邮件地址
         *
         * @see SendEmailRequest#setFromEmailAddress(String)
         */
        private String from;

        /**
         * 邮件主题
         *
         * @see SendEmailRequest#setSubject(String)
         */
        private String subject;

        /**
         * 模板 ID
         *
         * @see Template#setTemplateID(Long)
         */
        private Long templateId;

        /**
         * 模板变量名（按顺序填写）
         */
        private String[] variableNames;

        /**
         * 邮件触发类型
         *
         * @see SendEmailRequest#setTriggerType(Long)
         */
        private Long triggerType;

        // endregion

        /**
         * 验证有效期（单位：分钟）
         */
        private int validityPeriod;

    }

}

