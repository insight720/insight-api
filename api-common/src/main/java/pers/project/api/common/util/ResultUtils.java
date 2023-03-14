package pers.project.api.common.util;

import pers.project.api.common.enums.ErrorCodeEnum;
import pers.project.api.common.exception.ServiceException;
import pers.project.api.common.model.dto.response.BaseResponse;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * 结果工具类
 *
 * @author Luo Fei
 * @date 2023/3/14
 */
public abstract class ResultUtils {

    private static final Integer SUCCESS_CODE = 0;
    public static final String SUCCESS_MESSAGE = "ok";

    /**
     * 远程调用超时时间（单位秒）
     */
    public static final Long TIMEOUT = 3L;

    /**
     * 成功
     */
    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(SUCCESS_CODE, data, SUCCESS_MESSAGE);
    }

    /**
     * 失败
     *
     * @param errorCodeEnum 错误码枚举
     */
    public static BaseResponse<Void> failure(ErrorCodeEnum errorCodeEnum) {
        return new BaseResponse<>(errorCodeEnum);
    }

    /**
     * 失败
     *
     * @param exception 业务异常
     */
    public static BaseResponse<Void> failure(ServiceException exception) {
        return new BaseResponse<>(exception.getCode(), null, exception.getMessage());
    }

    /**
     * 失败
     *
     * @param errorCodeEnum 错误码枚举
     * @param message       信息
     */
    public static BaseResponse<Void> failure(ErrorCodeEnum errorCodeEnum, String message) {
        return new BaseResponse<>(errorCodeEnum.getCode(), null, message);
    }

    /**
     * 是否为失败响应
     *
     * @return 如果是失败响应，返回 true
     */
    public static <R> boolean isFailure(BaseResponse<R> response) {
        if (response == null || response.getData() == null) {
            return true;
        }
        return !SUCCESS_MESSAGE.equals(response.getMessage());
    }

    /**
     * 调用远程操作必须有超时设置。
     *
     * @see CompletableFuture#get(long, TimeUnit)
     */
    public static <R> R futureGet(CompletableFuture<R> future) throws Exception {
        return future.get(TIMEOUT, TimeUnit.SECONDS);
    }

}
