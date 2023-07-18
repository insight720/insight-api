package pers.project.api.common.util;

import pers.project.api.common.enumeration.ErrorEnum;
import pers.project.api.common.model.Result;

import static pers.project.api.common.enumeration.ErrorEnum.NO_ERROR;


/**
 * 通用响应结果工具类
 *
 * @author Luo Fei
 * @date 2023/03/14
 */
@SuppressWarnings("unused")
public abstract class ResultUtils {

    /**
     * 成功返回结果
     * <p>
     * 带有数据。
     *
     * @param data 返回结果数据
     * @param <T>  返回数据的类型
     * @return 响应结果对象
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(NO_ERROR.code(), NO_ERROR.message(), data);
    }

    /**
     * 成功返回结果
     * <p>
     * 不带数据。
     *
     * @return 响应结果对象
     */
    public static Result<Void> success() {
        return new Result<>(NO_ERROR.code(), NO_ERROR.message(), null);
    }

    /**
     * 失败返回结果
     * <p>
     * 使用 {@code ErrorEnum} 枚举定义的错误码和错误信息。
     *
     * @param errorEnum 错误枚举
     * @return 响应结果对象
     */
    public static <T> Result<T> failure(ErrorEnum errorEnum) {
        return new Result<>(errorEnum.code(), errorEnum.message(), null);
    }

    /**
     * 失败返回结果
     * <p>
     * 自定义错误信息。
     *
     * @param errorEnum 错误枚举
     * @param message   自定义错误信息
     * @return 响应结果对象
     */
    public static <T> Result<T> failure(ErrorEnum errorEnum, String message) {
        return new Result<>(errorEnum.code(), message, null);
    }

    /**
     * 失败返回结果
     * <p>
     * 自定义错误码和错误信息。
     *
     * @param code    自定义错误码
     * @param message 自定义错误信息
     * @return 响应结果对象
     */
    public static <T> Result<T> failure(String code, String message) {
        return new Result<>(code, message, null);
    }

    /**
     * 判断响应结果是否成功。
     *
     * @param result 响应结果对象
     * @return 返回 true 表示响应结果成功，否则表示失败。
     */
    public static boolean isSuccess(Result<?> result) {
        // 若返回码等于 NO_ERROR 的错误码则代表成功，根据实际情况可能需要更改判断条件
        return result != null && NO_ERROR.code().equals(result.getCode());
    }

    /**
     * 判断响应结果是否失败。
     *
     * @param result 响应结果对象
     * @return 返回 true 表示响应结果失败，否则表示成功。
     */
    public static boolean isFailure(Result<?> result) {
        // 若返回码不等于 NO_ERROR 的错误码则代表失败，根据实际情况可能需要更改判断条件
        return result == null || !NO_ERROR.code().equals(result.getCode());
    }

}
