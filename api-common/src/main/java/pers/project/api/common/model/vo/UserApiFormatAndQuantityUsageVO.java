package pers.project.api.common.model.vo;

import lombok.Data;

/**
 * 用户 API 格式和计数用法 VO
 *
 * @author Luo Fei
 * @date 2023/05/10
 */
@Data
public class UserApiFormatAndQuantityUsageVO {

    // region Same with ApiFormatPo
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
    // endregion

    // region Same with UserQuantityUsagePo
    /**
     * 总调用次数
     */
    private Long total;

    /**
     * 失败调用次数
     */
    private Long failure;

    /**
     * 调用次数存量
     */
    private Long stock;

    /**
     * 用法状态
     */
    private Integer usageStatus;
    // endregion

}
