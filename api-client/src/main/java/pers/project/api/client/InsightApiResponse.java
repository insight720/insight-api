package pers.project.api.client;

import java.net.http.HttpHeaders;

/**
 * Insight API 的响应接口
 *
 * @author Luo Fei
 * @date 2023/07/11
 */
public interface InsightApiResponse<T> {

    /**
     * 获取响应的状态码。
     *
     * @return 状态码值
     */
    int statusCode();

    /**
     * 获取响应头信息。
     *
     * @return HTTP 响应头的只读视图
     */
    HttpHeaders responseHeader();

    /**
     * 获取响应体内容。
     *
     * @return 响应体对象
     */
    T body();

}
