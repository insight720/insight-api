package pers.project.api.common.exception;

import lombok.experimental.StandardException;

/**
 * 数据不一致异常
 * <p>
 * 在发生预期之外的数据不一致时抛出的异常，通常需要在 {@code message} 中添加不一致数据信息。
 *
 * @author Luo Fei
 * @date 2023/07/06
 */
@StandardException
public class DataInconsistencyException extends RuntimeException {
}
