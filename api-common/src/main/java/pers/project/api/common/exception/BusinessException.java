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

    private final String code;

    public BusinessException(ErrorEnum errorEnum) {
        super(errorEnum.getMessage());
        this.code = errorEnum.getCode();
    }

    public BusinessException(ErrorEnum errorEnum, String message) {
        super(message);
        this.code = errorEnum.getCode();
    }

}
