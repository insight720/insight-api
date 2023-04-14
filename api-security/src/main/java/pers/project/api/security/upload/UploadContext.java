package pers.project.api.security.upload;

import org.springframework.web.multipart.MultipartFile;
import pers.project.api.security.enumeration.UploadFileEnum;
import pers.project.api.security.execption.UploadContextException;

/**
 * 上传上下文
 *
 * @author Luo Fei
 * @date 2023/04/14
 */
public interface UploadContext {

    /**
     * 上传
     *
     * @param userProfileId  用户资料 ID
     * @param file           上传的文件
     * @param uploadFileEnum 上传文件对应的枚举
     * @return 文件 URL
     * <P>（以 http/https 协议头开始，不以分隔符 / 结尾）
     * @throws UploadContextException 如果上传出现错误
     */
    String upload(String userProfileId, MultipartFile file, UploadFileEnum uploadFileEnum) throws UploadContextException;

    /**
     * 删除
     *
     * @param fileUrl        文件 URL
     *                       <p>（以 http/https 协议头开始，不以分隔符 / 结尾）
     * @param uploadFileEnum 上传文件对应的枚举
     * @throws UploadContextException 如果删除出现错误
     */
    void delete(String fileUrl, UploadFileEnum uploadFileEnum) throws UploadContextException;

}
