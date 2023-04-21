package pers.project.api.common.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 响应错误枚举
 *
 * @author Luo Fei
 * @date 2023/03/14
 */
@Getter
@AllArgsConstructor
public enum ErrorEnum {

    NO_ERROR("00000", "ok"),

    SERVICE_ERROR("A0500", "用户请求服务异常"),

    AUTHENTICATION_ERROR("A0200", "用户登录异常"),

    AUTHORIZATION_ERROR("A0300", "访问权限异常"),


    PARAM_ERROR("A0400", "用户请求参数错误"),

    SERVER_ERROR("B0001", "系统执行出错"),

    REGISTRY_ERROR("A0100", "用户注册错误"),

    // 确定保留
    DATABASE_ERROR("C0300", "数据库服务出错"),
    LOGIN_ERROR("A0200", "用户登录异常"),
    VERIFICATION_CODE_ERROR("A0240", "用户验证码错误"),
    UPLOAD_ERROR("A0700", "用户上传文件异常");

    private final String code;

    private final String message;

}
