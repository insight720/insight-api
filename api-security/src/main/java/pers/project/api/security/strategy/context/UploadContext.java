package pers.project.api.security.strategy.context;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import pers.project.api.security.execption.UploadException;
import pers.project.api.security.strategy.UploadStrategy;

/**
 * 上传策略上下文
 *
 * @author Luo Fei
 * @date 2023/03/28
 */
@Component
public final class UploadContext {

    private static UploadStrategy uploadStrategy;

    private UploadContext() {
    }

    @Autowired
    public void setUploadStrategy(UploadStrategy uploadStrategy) {
        UploadContext.uploadStrategy = uploadStrategy;
    }

    /**
     * 执行文件上传策略
     *
     * @param file         上传文件
     * @param directoryUri 上传目录 URI
     *                     <P>（以分隔符 / 结尾，不以分隔符 / 开始）
     * @return 文件访问 URL
     * @throws UploadException 上传异常
     */
    public static String executeStrategy(MultipartFile file, String directoryUri) throws UploadException {
        return uploadStrategy.uploadFile(file, directoryUri);
    }

}
