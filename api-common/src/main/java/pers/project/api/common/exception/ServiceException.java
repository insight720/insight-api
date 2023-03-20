package pers.project.api.common.exception;


import lombok.Getter;
import pers.project.api.common.constant.enumeration.ErrorEnum;

/**
 * 服务异常
 *
 * @author Luo Fei
 * @version 2023/3/14
 */
@Getter
public class ServiceException extends RuntimeException {

    private final String code;

    public ServiceException(ErrorEnum errorEnum) {
        super(errorEnum.getMessage());
        this.code = errorEnum.getCode();
    }

    public ServiceException(ErrorEnum errorEnum, String message) {
        super(message);
        this.code = errorEnum.getCode();
    }

}
