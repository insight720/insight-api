package pers.project.api.security.strategy.impl;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;
import pers.project.api.security.config.properties.UploadProperties;
import pers.project.api.security.execption.UploadException;
import pers.project.api.security.strategy.UploadStrategy;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.apache.commons.io.FilenameUtils.EXTENSION_SEPARATOR;

/**
 * OSS 上传策略（默认）
 *
 * @author Luo Fei
 * @date 2023/03/28
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "insight-api.upload", name = "strategy",
        havingValue = "cos", matchIfMissing = true)
public class CosUploadStrategyImpl implements UploadStrategy {

    private final COSClient cosClient;

    private final UploadProperties.CosProperties properties;

    @Override
    public String uploadFile(MultipartFile file, String directoryUri) throws UploadException {
        byte[] fileBytes;
        String md5Digest, contentMd5;
        try (InputStream inputStream = file.getInputStream()) {
            if (log.isDebugEnabled()) {
                // sun.nio.ch.ChannelInputStream 数据只能读取一次
                log.debug("""
                        Upload input stream type is: {}, file data may only be read once
                        """, inputStream.getClass());
            }
            // 不能上传大文件，否则可能产生 OOM
            fileBytes = IOUtils.toByteArray(inputStream);
            byte[] md5Bytes = DigestUtils.md5Digest(fileBytes);
            md5Digest = Hex.encodeHexString(md5Bytes);
            contentMd5 = Base64.encodeBase64String(md5Bytes);
        } catch (IOException e) {
            throw new UploadException(e);
        }
        // 文件内容的 MD5 摘要生成新文件名
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        if (extension == null) {
            throw new UploadException("File extension not found");
        }
        String newFileName = md5Digest + EXTENSION_SEPARATOR + extension;
        // 不上传重复的文件
        String bucketName = properties.getBucketName();
        String fileUri = directoryUri + newFileName;
        boolean exist = cosClient.doesObjectExist(bucketName, newFileName);
        if (exist) {
            return properties.getDomainName() + fileUri;
        }
        // 设置请求头并上传文件
        ObjectMetadata metadata = new ObjectMetadata();
        String contentType = file.getContentType();
        if (contentType != null) {
            metadata.setContentType(contentType);
        }
        metadata.setContentLength(fileBytes.length);
        // 避免 COS 客户端重复计算 MD5 摘要
        metadata.setContentMD5(contentMd5);
        try {
            cosClient.putObject(bucketName, fileUri, new ByteArrayInputStream(fileBytes), metadata);
        } catch (CosClientException e) {
            throw new UploadException(e);
        }
        return properties.getDomainName() + fileUri;
    }

}
