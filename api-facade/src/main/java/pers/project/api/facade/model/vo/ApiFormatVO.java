package pers.project.api.facade.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * API 格式 VO
 *
 * @author Luo Fei
 * @date 2023/05/29
 */
@Data
public class ApiFormatVO {

    // region Same with ApiFormatPO
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
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    // endregion

}
