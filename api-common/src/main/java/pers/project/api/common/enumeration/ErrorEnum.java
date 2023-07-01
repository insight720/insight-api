package pers.project.api.common.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * 通用错误枚举
 * <p>
 * 统一定义各类错误码。
 *
 * @author Luo Fei
 * @date 2023/03/14
 */
@Getter
@Accessors(fluent = true)
@AllArgsConstructor
public enum ErrorEnum {

    NO_ERROR("00000", "ok"),

    REGISTRY_ERROR("A0100", "用户注册错误"),

    LOGIN_ERROR("A0200", "用户登录异常"),

    PASSWORD_ERROR("A0210", "用户密码错误"),

    VERIFICATION_CODE_ERROR("A0240", "用户验证码错误"),

    ACCESS_ERROR("A0300", "访问权限异常"),

    PARAM_ERROR("A0400", "用户请求参数错误"),

    QUANTITY_EXCEEDED_ERROR("A0425", "数量超出限制"),

    USER_REQUEST_ERROR("A0500", "用户请求服务异常"),

    UPLOAD_ERROR("A0700", "用户上传文件异常"),

    SERVER_ERROR("B0001", "系统执行出错"),

    SYSTEM_RESOURCE_ERROR("B0300", "系统资源异常"),

    RPC_ERROR("C0110", "RPC 服务出错"),

    MESSAGE_DELIVERY_ERROR("C0121", "消息投递出错"),

    DATABASE_ERROR("C0300", "数据库服务出错");


    /**
     * 错误码
     * <p>
     * 必须唯一，由数字和字母组成，5位长度。
     */
    private final String code;

    /**
     * 基本错误信息
     * <p>
     * 可以不使用。
     */
    private final String message;

}
