package pers.project.api.common.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户 API 摘要 VO
 *
 * @author Luo Fei
 * @date 2023/05/04
 */
@Data
public class UserApiDigestVO {

    /**
     * 主键
     */
    private String digestId;

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

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

}
