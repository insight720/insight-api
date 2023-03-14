package pers.project.api.common.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pers.project.api.common.enums.ErrorCodeEnum;

/**
 * 基本响应
 *
 * @author Luo Fei
 * @date 2023/3/14
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseResponse<T> {

    /**
     * 状态码
     */
    private Integer code;

    /**
     * 数据
     */
    private T data;

    /**
     * 信息
     */
    private String message;

    public BaseResponse(ErrorCodeEnum errorCodeEnum) {
        this(errorCodeEnum.getCode(), null, errorCodeEnum.getMessage());
    }

}
