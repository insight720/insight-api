package pers.project.api.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pers.project.api.common.enumeration.ErrorEnum;

/**
 * 通用响应结果
 *
 * @param <T> 返回数据的类型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {

    /**
     * 响应码
     * <p>
     * 代表请求处理状态。
     *
     * @see ErrorEnum#code()
     */
    private String code;

    /**
     * 响应消息
     * <p>
     * 代表请求处理结果描述。
     */
    private String message;

    /**
     * 数据对象
     * <p>
     * 通常为业务数据。
     */
    private T data;

}
