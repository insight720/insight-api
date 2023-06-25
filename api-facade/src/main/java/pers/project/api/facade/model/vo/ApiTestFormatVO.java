package pers.project.api.facade.model.vo;

import lombok.Data;

/**
 * API 测试调用格式 VO
 *
 * @author Luo Fei
 * @date 2023/06/08
 */
@Data
public class ApiTestFormatVO {

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
    // endregion

}
