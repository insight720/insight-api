package pers.project.api.client.impl;


import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;
import org.springframework.util.Assert;
import pers.project.api.client.InsightApiClient;
import pers.project.api.client.InsightApiRequest;
import pers.project.api.client.InsightApiResponse;
import pers.project.api.client.exeception.InsightApiClientException;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import static org.springframework.util.StringUtils.hasText;
import static pers.project.api.client.enumaration.SignatureRequestHeaderEnum.*;
import static pers.project.api.client.util.SignatureHeaderUtils.*;

/**
 * Insight API 的客户端接口实现
 *
 * @author Luo Fei
 * @date 2023/07/11
 */
public final class InsightClientImpl implements InsightApiClient {

    private static final String QUESTION_MARK = "?";

    private static final String EQUALS_SIGN = "=";

    private static final String AMPERSAND = "&";

    private static final String OPEN_BRACE = "{";

    private static final String CLOSE_BRACE = "}";

    /**
     * {@code InsightApiClient} 的构建器实现
     */
    public static final class InsightApiClientBuilderImpl implements Builder {

        private String secretId;

        private String secretKey;

        private Executor executor;

        private Duration connectTimeout;

        @Override
        public Builder secretId(String secretId) {
            this.secretId = secretId;
            return this;
        }

        @Override
        public Builder secretKey(String secretKey) {
            this.secretKey = secretKey;
            return this;
        }

        @Override
        public Builder executor(Executor executor) {
            this.executor = executor;
            return this;
        }

        @Override
        public Builder connectTimeout(Duration connectTimeout) {
            this.connectTimeout = connectTimeout;
            return this;
        }

        @Override
        public InsightApiClient build() {
            return new InsightClientImpl(this);
        }

    }

    private final HttpClient httpClient;

    private final String secretId;

    private final String secretKey;

    private InsightClientImpl(InsightApiClientBuilderImpl builder) {
        Assert.hasText(builder.secretId, "The secretId must have text");
        Assert.hasText(builder.secretKey, "The secretKey must have text");
        this.secretId = builder.secretId;
        this.secretKey = builder.secretKey;
        HttpClient.Builder httpClientBuilder = HttpClient.newBuilder();
        if (builder.executor != null) {
            httpClientBuilder.executor(builder.executor);
        }
        if (builder.connectTimeout != null) {
            httpClientBuilder.connectTimeout(builder.connectTimeout);
        }
        this.httpClient = httpClientBuilder.build();
    }

    @Override
    public <T> InsightApiResponse<T> send(InsightApiRequest insightApiRequest, Class<T> responseBodyClass) {
        checkSenderParameters(insightApiRequest, responseBodyClass);
        HttpResponse<?> httpResponse;
        try {
            HttpRequest httpRequest = buildHttpRequest(insightApiRequest);
            httpResponse = httpClient.send(httpRequest, getBodyHandlers(responseBodyClass));
        } catch (Exception e) {
            throw new InsightApiClientException(e);
        }
        return getInsightApiResponse(responseBodyClass, httpResponse);
    }

    @Override
    public <T> CompletableFuture<InsightApiResponse<T>> sendAsync
            (InsightApiRequest insightApiRequest, Class<T> responseBodyClass) {
        checkSenderParameters(insightApiRequest, responseBodyClass);
        CompletableFuture<? extends HttpResponse<?>> future;
        try {
            HttpRequest httpRequest = buildHttpRequest(insightApiRequest);
            future = httpClient.sendAsync(httpRequest, getBodyHandlers(responseBodyClass));
        } catch (Exception e) {
            throw new InsightApiClientException(e);
        }
        // future 的工作线程完成任务后转换，异常不会传递，需要 exceptionally 处理
        return future.thenApply(httpResponse ->
                getInsightApiResponse(responseBodyClass, httpResponse));
    }

    /**
     * 检查发送请求时的参数
     *
     * @param insightApiRequest Insight API 请求对象
     * @param responseBodyClass 响应体的类型
     */
    private static <T> void checkSenderParameters(InsightApiRequest insightApiRequest, Class<T> responseBodyClass) {
        Assert.notNull(insightApiRequest, "The insightApiRequest must be not null");
        Assert.notNull(responseBodyClass, "The responseBodyClass must be not null");
    }

    /**
     * 获取 Insight API 响应对象
     *
     * @param responseBodyClass 响应体的类型
     * @param httpResponse      原始 HTTP 响应对象
     * @param <T>               响应体的泛型
     * @return Insight API 响应对象
     */
    @SuppressWarnings("unchecked")
    private static <T> InsightApiResponseImpl<T> getInsightApiResponse
    (Class<T> responseBodyClass, HttpResponse<?> httpResponse) {
        T t;
        // 用户需要 InputStream，byte[] 或 String（通常是 JSON 字符串）类型的响应体
        if (InputStream.class.isAssignableFrom(responseBodyClass)
                || byte[].class == responseBodyClass
                || String.class == responseBodyClass) {
            t = (T) httpResponse.body();
        } else {
            // 用户需要其他类型的响应体 responseBodyClass 是必须由 JSON 字符串转换的类型
            try {
                t = JSON.parseObject((String) httpResponse.body(), responseBodyClass, JSONReader.Feature.FieldBased);
            } catch (Exception e) {
                throw new InsightApiClientException(e);
            }
        }
        return new InsightApiResponseImpl<>(httpResponse.statusCode(), httpResponse.headers(), t);
    }

    /**
     * 获取请求体处理器
     *
     * @param responseBodyClass 响应体的类型
     * @return 请求体处理器
     */
    private static HttpResponse.BodyHandler<?> getBodyHandlers(Class<?> responseBodyClass) {
        // 用户需要 InputStream 或 byte[] 类型的响应体
        if (InputStream.class.isAssignableFrom(responseBodyClass)) {
            return HttpResponse.BodyHandlers.ofInputStream();
        }
        if (byte[].class == responseBodyClass) {
            return HttpResponse.BodyHandlers.ofByteArray();
        }
        // 用户需要 String（通常是 JSON 字符串）或 由 JSON 字符串转换的类型
        return HttpResponse.BodyHandlers.ofString();
    }

    /**
     * 构建 HTTP 请求对象
     *
     * @param insightApiRequest Insight API 请求对象
     * @return HTTP 请求对象
     */
    private HttpRequest buildHttpRequest(InsightApiRequest insightApiRequest) {
        HttpRequest.Builder httpRequestBuilder = HttpRequest.newBuilder();
        // 设置 method 和 requestBody
        HttpRequest.BodyPublisher bodyPublisher = insightApiRequest.requestBody()
                .map(body -> {
                    // inputStream 和 byte[] 类型用于传递流数据
                    if (InputStream.class.isAssignableFrom(body.getClass())) {
                        return HttpRequest.BodyPublishers.ofInputStream(() -> (InputStream) body);
                    }
                    if (body instanceof byte[] bytes) {
                        return HttpRequest.BodyPublishers.ofByteArray(bytes);
                    }
                    // 其他类型转换为 JSON 字符串
                    String jsonString = body instanceof String
                            ? (String) body : JSON.toJSONString(body, JSONWriter.Feature.FieldBased);
                    return HttpRequest.BodyPublishers.ofString(jsonString);
                })
                // 没有请求体
                .orElseGet(HttpRequest.BodyPublishers::noBody);
        // method 设置为大写
        httpRequestBuilder.method(insightApiRequest.method().toUpperCase(), bodyPublisher);
        // 替换 url 的 pathVariable 占位符
        StringBuilder urlWithVariablesBuilder = insightApiRequest.pathVariable()
                .map(variableMap -> {
                    StringBuilder builder = new StringBuilder(insightApiRequest.url());
                    variableMap.forEach((variableName, variableValue) -> {
                        if (hasText(variableName) && hasText(variableName)) {
                            String placeholder = OPEN_BRACE + variableName + CLOSE_BRACE;
                            int startIndex = builder.indexOf(placeholder);
                            if (startIndex == -1) {
                                String message = "There is no placeholder %s in the URL %s"
                                        .formatted(placeholder, insightApiRequest.url());
                                throw new InsightApiClientException(message);
                            }
                            int endIndex = startIndex + placeholder.length();
                            builder.replace(startIndex, endIndex, variableValue);
                        }
                    });
                    return builder;
                }).orElse(new StringBuilder(insightApiRequest.url()));
        // 拼接 url 后的 requestParam
        String urlWithVariablesAndParams = insightApiRequest.requestParam()
                .map(paramMap -> {
                    // 拼接 ?
                    urlWithVariablesBuilder.append(QUESTION_MARK);
                    Set<Map.Entry<String, String>> entrySet = paramMap.entrySet();
                    Iterator<Map.Entry<String, String>> iterator = entrySet.iterator();
                    // 拼接第一个参数 key=value
                    Map.Entry<String, String> firestEntry = iterator.next();
                    String firstParamName = firestEntry.getKey();
                    String firstParamValue = firestEntry.getValue();
                    if (hasText(firstParamName) && hasText(firstParamValue)) {
                        urlWithVariablesBuilder.append(firstParamName)
                                .append(EQUALS_SIGN).append(firstParamValue);
                    }
                    // 拼接其余参数 &key=value
                    iterator.forEachRemaining(entry -> {
                        String paramName = entry.getKey();
                        String paramValue = entry.getValue();
                        if (hasText(paramName) && hasText(paramName)) {
                            urlWithVariablesBuilder.append(AMPERSAND).append(paramName)
                                    .append(EQUALS_SIGN).append(paramValue);
                        }
                    });
                    return urlWithVariablesBuilder.toString();
                })
                .orElseGet(urlWithVariablesBuilder::toString);
        httpRequestBuilder.uri(URI.create(urlWithVariablesAndParams));
        // 添加请求头
        addSignatureHeaders(insightApiRequest, urlWithVariablesAndParams, httpRequestBuilder);
        insightApiRequest.requestHeader()
                .ifPresent(headerMap -> headerMap.forEach((headerName, headerValue) -> {
                    if (hasText(headerName) && hasText(headerValue)) {
                        httpRequestBuilder.header(headerName, headerValue);
                    }
                }));
        return httpRequestBuilder.build();
    }

    /**
     * 添加签名相关的请求头
     *
     * @param insightApiRequest  Insight API 请求对象
     * @param urlWithVariablesAndParams      带路径变量和请求参数的 URL
     * @param httpRequestBuilder HTTP 请求构建器
     */
    private void addSignatureHeaders
    (InsightApiRequest insightApiRequest, String urlWithVariablesAndParams, HttpRequest.Builder httpRequestBuilder) {
        httpRequestBuilder.header(USAGE_TYPE.getHeaderName(), insightApiRequest.usageType());
        httpRequestBuilder.header(ORIGINAL_URL.getHeaderName(), insightApiRequest.url());
        httpRequestBuilder.header(SECRET_ID.getHeaderName(), secretId);
        httpRequestBuilder.header(TIMESTAMP.getHeaderName(), getTimestamp());
        httpRequestBuilder.header(NONCE.getHeaderName(), getNonce());
        httpRequestBuilder.header(SIGN.getHeaderName(), getSign(secretKey, urlWithVariablesAndParams, insightApiRequest));
    }

    @Override
    public String secretId() {
        return this.secretId;
    }

    @Override
    public String secretKey() {
        return this.secretKey;
    }

    @Override
    public Optional<Executor> executor() {
        return this.httpClient.executor();
    }

    @Override
    public Optional<Duration> connectTimeout() {
        return this.httpClient.connectTimeout();
    }

}
