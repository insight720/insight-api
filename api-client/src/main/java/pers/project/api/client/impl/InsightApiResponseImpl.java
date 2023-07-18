package pers.project.api.client.impl;

import org.springframework.util.Assert;
import pers.project.api.client.InsightApiResponse;

import java.net.http.HttpHeaders;

/**
 * Insight API 的响应实现
 *
 * @author Luo Fei
 * @date 2023/07/12
 */
public record InsightApiResponseImpl<T>(int statusCode, HttpHeaders responseHeader,
                                        T body) implements InsightApiResponse<T> {

    public InsightApiResponseImpl {
        Assert.notNull(responseHeader, "The responseHeader must be not null");
    }

}
