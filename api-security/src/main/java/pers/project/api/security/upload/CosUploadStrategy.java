package pers.project.api.security.upload;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import pers.project.api.security.execption.UploadContextException;
import pers.project.api.security.properties.UploadContextProperties;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import static com.baomidou.mybatisplus.core.toolkit.StringPool.DOT;


/**
 * 腾讯云对象存储（COS）上传策略
 *
 * @author Luo Fei
 * @date 2023/03/28
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CosUploadStrategy implements UploadStrategy {

    private final COSClient cosClient;

    private final UploadContextProperties.CosProperties properties;

    @Override
    public String uploadByUserProfileId(String userProfileId, MultipartFile file, String directoryUri) throws UploadContextException {
        byte[] fileBytes;
        String contentMd5, fileAndUserProfileIdMd5;
        MessageDigest md5Digest = DigestUtils.getMd5Digest();
        try (InputStream inputStream = file.getInputStream()) {
            if (log.isDebugEnabled()) {
                // sun.nio.ch.ChannelInputStream 数据只能读取一次
                log.debug("""
                        Upload input stream type is: {}, file data may only be read once
                        """, inputStream.getClass());
            }
            // 不能上传大文件，否则可能产生 OOM
            fileBytes = inputStream.readAllBytes();
            md5Digest.update(fileBytes);
            // 调用 digest() 后 md5Digest 会被重置
            byte[] fileMd5Bytes = md5Digest.digest();
            contentMd5 = Base64.encodeBase64String(fileMd5Bytes);
            // 复用已重置的 md5Digest
            md5Digest.update(fileBytes);
            md5Digest.update(userProfileId.getBytes(StandardCharsets.UTF_8));
            fileAndUserProfileIdMd5 = Hex.encodeHexString(md5Digest.digest());
        } catch (IOException e) {
            throw new UploadContextException(e);
        }
        // 文件内容和用户资料 ID 的 MD5 摘要生成新文件名
        String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
        if (extension == null) {
            throw new UploadContextException("File extension not found");
        }
        String newFileName = fileAndUserProfileIdMd5 + DOT + extension;
        // 不上传已存在的文件
        String bucketName = properties.getBucketName();
        String fileUri = directoryUri + newFileName;
        boolean exist = cosClient.doesObjectExist(bucketName, fileUri);
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
            throw new UploadContextException(e);
        }
        return properties.getDomainName() + fileUri;
    }

    @Override
    public void deleteByUrl(String fileUrl) throws UploadContextException {
        int domainNameLength = properties.getDomainName().length();
        // substring 截取 domainName 之后的字符串
        String fileUri = fileUrl.substring(domainNameLength);
        try {
            cosClient.deleteObject(properties.getBucketName(), fileUri);
        } catch (CosClientException e) {
            throw new UploadContextException(e);
        }
    }

}
