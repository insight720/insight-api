package pers.project.api.common.exception;


import lombok.Getter;
import pers.project.api.common.enums.ErrorCodeEnum;

/**
 * 服务异常
 *
 * @author Luo Fei
 * @date 2023/3/14
 */
@Getter
public class ServiceException extends RuntimeException {

    /**
     * 错误码
     */
    private final Integer code;

    public ServiceException(ErrorCodeEnum errorCodeEnum) {
        super(errorCodeEnum.getMessage());
        this.code = errorCodeEnum.getCode();
    }

    public ServiceException(ErrorCodeEnum errorCodeEnum, String message) {
        super(message);
        this.code = errorCodeEnum.getCode();
    }

}
