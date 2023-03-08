package pers.project.api.security.model.dto.api_info;

import lombok.Data;

import java.io.Serializable;

/**
 * 接口调用请求
 *
 * @TableName product
 */
@Data
public class ApiInfoInvokeRequest implements Serializable {

    /**
     * 主键
     */
    private Long id;

    /**
     * 用户请求参数
     */
    private String requestParams;

    private static final long serialVersionUID = 1L;
}