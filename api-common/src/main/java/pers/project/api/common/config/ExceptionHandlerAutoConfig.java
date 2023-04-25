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
import pers.project.api.common.enumeration.ErrorEnum;
import pers.project.api.common.exception.BusinessException;
import pers.project.api.common.model.Result;
import pers.project.api.common.util.ResultUtils;

import java.util.Arrays;
import java.util.stream.Collectors;

import static pers.project.api.common.enumeration.ErrorEnum.PARAM_ERROR;

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

    @ExceptionHandler(value = BusinessException.class)
    @ResponseStatus(HttpStatus.OK)
    public Result<Void> serviceException(HttpServletRequest request,
                                         BusinessException exception) {
        if (log.isDebugEnabled()) {
            log.warn("服务异常: {}", exception.getMessage());
            log.warn("服务异常 URI: {}", request.getRequestURI());
        }
        return ResultUtils.failure(exception.getCode(), exception.getMessage());
    }

    // region 参数验证

    @ExceptionHandler(value = ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> constraintViolationException(HttpServletRequest request,
                                                     ConstraintViolationException exception) {
        // 参数验证异常: methodName.fieldName: message, methodName.fieldName: message
        String message = exception.getMessage();
        log.warn("参数验证异常: {}", message);
        log.warn("参数验证异常 URI: {}", request.getRequestURI());
        // fieldName: message, fieldName: message
        String content = Arrays.stream(message.split(","))
                .map(s -> s.trim().substring(s.indexOf('.') + 1))
                .collect(Collectors.joining(", "));
        return ResultUtils.failure(PARAM_ERROR, message);
    }

    @ExceptionHandler(value = BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> bindException(HttpServletRequest request, BindException exception) {
        // 参数验证异常: objectName.fieldName: message
        String objectName = exception.getBindingResult().getObjectName();
        String message = exception.getFieldErrors()
                .stream()
                .map(fieldError -> String.format("%s.%s: %s",
                        objectName, fieldError.getField(), fieldError.getDefaultMessage()))
                .collect(Collectors.joining(", "));
        log.warn("参数验证异常: {}", message);
        log.warn("参数验证异常 URI: {}", request.getRequestURI());
        return ResultUtils.failure(PARAM_ERROR, message);
    }

    // endregion

    @ExceptionHandler(value = Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Void> throwable(HttpServletRequest request, Throwable throwable) {
        log.error("未知异常: ", throwable);
        log.error("未知异常 URI: {}", request.getRequestURI());
        return ResultUtils.failure(ErrorEnum.SERVER_ERROR);
    }

}
