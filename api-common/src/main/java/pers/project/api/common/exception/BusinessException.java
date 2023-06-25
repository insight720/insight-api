package pers.project.api.common.exception;


import lombok.Getter;
import pers.project.api.common.enumeration.ErrorEnum;
import pers.project.api.common.model.Result;

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
        super(errorEnum.message());
        this.code = errorEnum.code();
    }

    public BusinessException(ErrorEnum errorEnum, String message) {
        super(message);
        this.code = errorEnum.code();
    }

    public BusinessException(String code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(Result<String> result) {
        super(result.getMessage());
        this.code = result.getCode();
    }

    public BusinessException(Result<String> result, String message) {
        super(message);
        this.code = result.getCode();
    }

}
