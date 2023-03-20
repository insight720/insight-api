package pers.project.api.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pers.project.api.common.constant.enumeration.ErrorEnum;

/**
 * 通用响应
 *
 * @author Luo Fei
 * @date 2023/03/14
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Response<T> {

    private String code;

    private T data;

    private String message;

    public Response(ErrorEnum errorEnum) {
        this(errorEnum.getCode(), null, errorEnum.getMessage());
    }

}
