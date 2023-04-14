package pers.project.api.common.exception;


import lombok.Getter;
import pers.project.api.common.enumeration.ErrorEnum;

/**
 * 业务异常
 *
 * @author Luo Fei
 * @date 2023/03/14
 */
@Getter
public class BusinessException extends RuntimeException {

    private final ErrorEnum errorEnum;

    public BusinessException(ErrorEnum errorEnum) {
        super(errorEnum.getMessage());
        this.errorEnum = errorEnum;
    }

    public BusinessException(ErrorEnum errorEnum, String message) {
        super(message);
        this.errorEnum = errorEnum;
    }

}
