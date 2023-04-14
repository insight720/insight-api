package pers.project.api.common.exception;


import lombok.Getter;
import pers.project.api.common.enumeration.ErrorEnum;

/**
 * 服务器异常
 *
 * @author Luo Fei
 * @date 2023/03/21
 */
@Getter
public class ServerException extends RuntimeException {

    private final String code;

    public ServerException(ErrorEnum errorEnum) {
        super(errorEnum.getMessage());
        this.code = errorEnum.getCode();
    }

    public ServerException(ErrorEnum errorEnum, String message) {
        super(message);
        this.code = errorEnum.getCode();
    }

}
