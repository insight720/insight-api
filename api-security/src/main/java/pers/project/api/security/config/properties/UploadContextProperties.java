package pers.project.api.security.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 上传上下文属性
 *
 * @author Luo Fei
 * @date 2023/03/28
 */
@Data
@ConfigurationProperties(prefix = "insight-api.upload")
public class UploadContextProperties {

    /**
     * COS 上传属性
     *
     * @see <a href="https://cloud.tencent.com/document/product/436/7751">API 文档简介</a>
     */
    @Data
    @ConfigurationProperties(prefix = "insight-api.upload.cos")
    public static class CosProperties {

        /**
         * 开发者拥有的项目身份识别 ID
         */
        private String secretId;

        /**
         * 开发者拥有的项目身份密钥
         */
        private String secretKey;

        /**
         * 地域信息
         */
        private String region;

        /**
         * 存储桶名
         */
        private String bucketName;

        /**
         * 存储桶访问域名（以分隔符 / 结尾）
         */
        private String domainName;

    }

}
