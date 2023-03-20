package pers.project.api.common.constant.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 错误枚举
 *
 * @author Luo Fei
 * @version 2023/3/14
 */
@Getter
@AllArgsConstructor
public enum ErrorEnum {

    NO_ERROR("0000", "ok"),
    PARAMS_ERROR("40000", "请求参数错误"),
    NOT_LOGIN_ERROR("40100", "未登录"),
    NO_AUTH_ERROR("40101", "无权限"),
    NOT_FOUND_ERROR("40400", "请求数据不存在"),
    FORBIDDEN_ERROR("40300", "禁止访问"),
    SYSTEM_ERROR("50000", "系统内部异常"),
    OPERATION_ERROR("50001", "操作失败");

    private final String code;

    private final String message;

}
