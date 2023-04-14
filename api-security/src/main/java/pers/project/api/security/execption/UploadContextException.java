package pers.project.api.security.execption;

import lombok.experimental.StandardException;
import pers.project.api.security.upload.UploadContext;

/**
 * 上传上下文异常
 * <p>
 * 对 {@link UploadContext} 产生异常的包装。
 *
 * @author Luo Fei
 * @date 2023/03/30
 */
@StandardException
public class UploadContextException extends Exception {
}
