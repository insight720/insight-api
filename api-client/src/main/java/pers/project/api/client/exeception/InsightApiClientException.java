package pers.project.api.client.exeception;

/**
 * Insight API 客户端异常
 * <p>
 * 异常不再细分。
 *
 * @author Luo Fei
 * @date 2023/07/11
 */
public class InsightApiClientException extends RuntimeException {

    public InsightApiClientException() {
    }

    public InsightApiClientException(String message) {
        super(message);
    }

    public InsightApiClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public InsightApiClientException(Throwable cause) {
        super(cause);
    }

    public InsightApiClientException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
