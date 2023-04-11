package pers.project.api.security.execption;

/**
 * 上传异常
 *
 * @author Luo Fei
 * @date 2023/03/30
 */
public class UploadException extends Exception {

    public UploadException(String message) {
        super(message);
    }

    public UploadException(String message, Throwable cause) {
        super(message, cause);
    }

    public UploadException(Throwable cause) {
        super(cause);
    }

}
