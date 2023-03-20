package pers.project.api.facade.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pers.project.api.common.constant.enumeration.ErrorEnum;
import pers.project.api.common.exception.ServiceException;
import pers.project.api.common.model.Response;
import pers.project.api.common.util.ResponseUtils;

/**
 * 全局异常处理器
 *
 * @author yupi
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ServiceException.class)
    public Response<?> businessExceptionHandler(ServiceException e) {
        log.error("businessException: " + e.getMessage(), e);
        return ResponseUtils.failure(e);
    }

    @ExceptionHandler(RuntimeException.class)
    public Response<?> runtimeExceptionHandler(RuntimeException e) {
        log.error("runtimeException", e);
        return ResponseUtils.failure(ErrorEnum.SYSTEM_ERROR, e.getMessage());
    }

}
