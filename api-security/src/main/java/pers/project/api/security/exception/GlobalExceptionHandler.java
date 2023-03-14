package pers.project.api.security.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pers.project.api.common.enums.ErrorCodeEnum;
import pers.project.api.common.exception.ServiceException;
import pers.project.api.common.model.dto.response.BaseResponse;
import pers.project.api.common.util.ResultUtils;


/**
 * 全局异常处理器
 *
 * @author yupi
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ServiceException.class)
    public BaseResponse<?> businessExceptionHandler(ServiceException e) {
        log.error("businessException: " + e.getMessage(), e);
        return ResultUtils.failure(e);
    }

    @ExceptionHandler(RuntimeException.class)
    public BaseResponse<?> runtimeExceptionHandler(RuntimeException e) {
        log.error("runtimeException", e);
        return ResultUtils.failure(ErrorCodeEnum.SYSTEM_ERROR, e.getMessage());
    }

}
