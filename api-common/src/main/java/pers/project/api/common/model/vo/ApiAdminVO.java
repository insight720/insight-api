package pers.project.api.common.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 接口管理 VO
 *
 * @author Luo Fei
 * @date 2023/05/23
 */
@Data
public class ApiAdminVO {

    /**
     * 接口摘要更新时间
     */
    private LocalDateTime digestUpdateTime;

    /**
     * 接口格式主键
     */
    private String formatId;

    /**
     * 接口格式更新时间
     */
    private LocalDateTime formatUpdateTime;

    // region Same with ApiDigestPo
    /**
     * 接口名称
     */
    private String apiName;

    /**
     * 接口描述
     */
    private String description;

    /**
     * 请求方法
     */
    private Integer method;

    /**
     * 接口地址
     */
    private String url;

    /**
     * 接口用法类型
     */
    private String usageType;

    /**
     * 接口状态
     */
    private Integer apiStatus;
    // endregion

    // region Same with ApiFormatPo
    /**
     * 接口摘要主键
     */
    private String digestId;

    /**
     * 请求参数
     */
    private String requestParam;

    /**
     * 请求头
     */
    private String requestHeader;

    /**
     * 请求体
     */
    private String requestBody;

    /**
     * 响应头
     */
    private String responseHeader;

    /**
     * 响应体
     */
    private String responseBody;

    /**
     * 是否删除（1 表示删除，0 表示未删除）
     */
    private Integer isDeleted;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    // endregion

}
