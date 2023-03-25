package pers.project.api.common.util;

import pers.project.api.common.constant.enumeration.ErrorEnum;
import pers.project.api.common.model.Result;

import static pers.project.api.common.constant.enumeration.ErrorEnum.NO_ERROR;


/**
 * 响应结果工具类
 *
 * @author Luo Fei
 * @date 2023/03/14
 */
public abstract class ResultUtils {

    /**
     * Success return with nothing.
     *
     * @param <T> data type
     * @return Result
     */
    public static Result<Void> success() {
        return success(null);
    }

    /**
     * Success return with data.
     *
     * @param <T> data type
     * @return Result
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(NO_ERROR.getCode(), NO_ERROR.getMessage(), data);
    }

    /**
     * Failed return with message and detail error information.
     *
     * @return Result
     */
//    public static Result<String> failure(String message) {
//        return failure(ErrorCode.SERVER_ERROR, message);
//    }

    /**
     * Failed return with errorCode and message.
     *
     * @param <T> data type
     * @return Result
     */
    public static <T> Result<T> failure(ErrorEnum errorEnum) {
        return new Result<>(errorEnum.getCode(), errorEnum.getMessage(), null);
    }

    /**
     * Failed return with errorCode, and message.
     *
     * @param <T> data type
     * @return Result
     */
    public static <T> Result<T> failure(ErrorEnum errorEnum, String message) {
        return new Result<>(errorEnum.getCode(), message, null);
    }

    public static boolean isFailure(Object o) {
        return false;
    }

}
