package pers.project.api.common.util;

import pers.project.api.common.constant.enumeration.ErrorEnum;
import pers.project.api.common.exception.ServiceException;
import pers.project.api.common.model.Response;

import static pers.project.api.common.constant.enumeration.ErrorEnum.NO_ERROR;

/**
 * 结果工具类
 *
 * @author Luo Fei
 * @date 2023/03/14
 */
public abstract class ResponseUtils {

    /**
     * 成功
     */
    public static <T> Response<T> success(T data) {
        return new Response<>(NO_ERROR.getCode(), data, NO_ERROR.getMessage());
    }

    /**
     * 失败
     *
     * @param errorEnum 错误枚举
     */
    public static Response<Void> failure(ErrorEnum errorEnum) {
        return new Response<>(errorEnum);
    }

    /**
     * 失败
     *
     * @param errorEnum 错误枚举
     * @param message   信息
     */
    public static Response<Void> failure(ErrorEnum errorEnum, String message) {
        return new Response<>(errorEnum.getCode(), null, message);
    }

    /**
     * 失败
     *
     * @param exception 服务异常
     */
    public static Response<Void> failure(ServiceException exception) {
        return new Response<>(exception.getCode(), null, exception.getMessage());
    }

    /**
     * 是否为失败响应
     *
     * @return 如果是失败响应，返回 true
     */
    public static <R> boolean isFailure(Response<R> response) {
        if (response == null || response.getData() == null) {
            return true;
        }
        return !NO_ERROR.getMessage().equals(response.getMessage());
    }

}
