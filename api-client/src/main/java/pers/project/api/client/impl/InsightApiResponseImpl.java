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
public class InsightApiResponseImpl<T> implements InsightApiResponse<T> {

    private final int statusCode;

    private final HttpHeaders responseHeader;

    private final T body;

    public InsightApiResponseImpl(int statusCode, HttpHeaders responseHeader, T body) {
        Assert.notNull(responseHeader, "The responseHeader must be not null");
        this.statusCode = statusCode;
        this.responseHeader = responseHeader;
        this.body = body;
    }

    @Override
    public int statusCode() {
        return this.statusCode;
    }

    @Override
    public HttpHeaders responseHeader() {
        return this.responseHeader;
    }

    @Override
    public T body() {
        return this.body;
    }

}
