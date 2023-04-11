package pers.project.api.security.strategy;

import org.springframework.web.multipart.MultipartFile;
import pers.project.api.security.execption.UploadException;

/**
 * 上传策略接口
 *
 * @author Luo Fei
 * @date 2023/03/28
 */
public interface UploadStrategy {

    /**
     * 上传文件
     *
     * @param file         上传文件
     * @param directoryUri 上传目录 URI
     *                     <P>（以分隔符 / 结尾，不以分隔符 / 开始）
     * @return 文件访问 URL
     * @throws UploadException 上传异常
     */
    String uploadFile(MultipartFile file, String directoryUri) throws UploadException;

}
