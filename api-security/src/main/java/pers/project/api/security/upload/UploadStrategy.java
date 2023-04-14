package pers.project.api.security.upload;

import org.springframework.web.multipart.MultipartFile;
import pers.project.api.security.execption.UploadContextException;

/**
 * 上传策略接口
 *
 * @author Luo Fei
 * @date 2023/03/28
 */
public interface UploadStrategy {

    /**
     * 按用户资料 ID 上传
     *
     * @param userProfileId 用户资料 ID
     * @param file          上传文件
     * @param directoryUri  上传目录 URI
     *                      <P>（以分隔符 / 结尾，不以分隔符 / 开始）
     * @return 文件访问 URL
     * @throws UploadContextException 如果上传异常
     */
    String uploadByUserProfileId(String userProfileId, MultipartFile file, String directoryUri) throws UploadContextException;

    /**
     * 根据 URL 删除
     *
     * @param fileUrl 文件 URL
     *                <P>（以 http/https 协议头开始，不以分隔符 / 结尾）
     * @throws UploadContextException 如果删除异常
     */
    void deleteByUrl(String fileUrl) throws UploadContextException;

}
