package pers.project.api.common.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pers.project.api.common.constant.enumeration.ErrorEnum;
import pers.project.api.common.exception.ServiceException;
import pers.project.api.common.model.Response;
import pers.project.api.common.util.ResponseUtils;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * 异常处理程序自动配置类
 *
 * @author Luo Fei
 * @date 2023/03/18
 */
@Slf4j
@AutoConfiguration
@RestControllerAdvice
@ConditionalOnWebApplication(type = Type.SERVLET)
public class ExceptionHandlerAutoConfig {

    @ExceptionHandler(value = ServiceException.class)
    @ResponseStatus(HttpStatus.OK)
    public Response<Void> serviceException(HttpServletRequest request,
                                           ServiceException exception) {
        if (log.isDebugEnabled()) {
            log.warn("服务异常: {}", exception.getMessage());
            log.warn("服务异常 URI: {}", request.getRequestURI());
        }
        return ResponseUtils.failure(exception);
    }

    // region 参数验证

    @ExceptionHandler(value = ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response<Void> constraintViolationException(HttpServletRequest request,
                                                       ConstraintViolationException exception) {
        // 参数验证异常: methodName.fieldName: message, methodName.fieldName: message
        String message = exception.getMessage();
        log.warn("参数验证异常: {}", message);
        log.warn("参数验证异常 URI: {}", request.getRequestURI());
        // fieldName: message, fieldName: message
        String content = Arrays.stream(message.split(","))
                .map(s -> s.trim().substring(s.indexOf('.') + 1))
                .collect(Collectors.joining(", "));
        return ResponseUtils.failure(ErrorEnum.PARAMS_ERROR, content);
    }

    @ExceptionHandler(value = BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response<Void> bindException(HttpServletRequest request, BindException exception) {
        // 参数验证异常: objectName.fieldName: message
        String objectName = exception.getBindingResult().getObjectName();
        String message = exception.getFieldErrors()
                .stream()
                .map(fieldError -> String.format("%s.%s: %s",
                        objectName, fieldError.getField(), fieldError.getDefaultMessage()))
                .collect(Collectors.joining(", "));
        log.warn("参数验证异常: {}", message);
        log.warn("参数验证异常 URI: {}", request.getRequestURI());
        return ResponseUtils.failure(ErrorEnum.PARAMS_ERROR, message);
    }

    // endregion

    @ExceptionHandler(value = Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Response<Void> throwable(HttpServletRequest request, Throwable throwable) {
        log.error("未知异常: ", throwable);
        log.error("未知异常 URI: {}", request.getRequestURI());
        return ResponseUtils.failure(ErrorEnum.SYSTEM_ERROR);
    }

}
