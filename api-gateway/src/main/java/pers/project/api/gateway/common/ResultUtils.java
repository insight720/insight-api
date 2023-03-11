package pers.project.api.gateway.common;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 返回工具类
 *
 * @author yupi
 */
public class ResultUtils {
// TODO: 2023/2/25 ok 不能改

    /**
     * 成功
     *
     * @param data
     * @param <T>
     * @return
     */
    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(0, data, "ok");
    }

    /**
     * 失败
     *
     * @param errorCode
     * @return
     */
    public static BaseResponse error(ErrorCode errorCode) {
        return new BaseResponse<>(errorCode);
    }

    /**
     * 失败
     *
     * @param code
     * @param message
     * @return
     */
    public static BaseResponse error(int code, String message) {
        return new BaseResponse(code, null, message);
    }

    /**
     * 失败
     *
     * @param errorCode
     * @return
     */
    public static BaseResponse error(ErrorCode errorCode, String message) {
        return new BaseResponse(errorCode.getCode(), null, message);
    }

    public static boolean isError(BaseResponse<?> response) {
        if (response == null) {
            return true;
        }
        if (response.getData() == null) {
            return true;
        }
        return !"ok".equals(response.getMessage());
    }

    /**
     * 在超时时间内从 CompletableFuture 中获取结果，抛出所有异常
     *
     * @param future
     * @param <R>
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     * @throws TimeoutException
     */
    public static <R> R getFromFuture(CompletableFuture<R> future)
            throws ExecutionException, InterruptedException, TimeoutException {
        return future.get(1000, TimeUnit.MILLISECONDS);
    }

}
