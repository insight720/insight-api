package pers.project.api.client.impl;

import org.springframework.util.Assert;
import pers.project.api.client.InsightApiRequest;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;

import static org.springframework.util.StringUtils.hasText;

/**
 * Insight API 的请求实现
 *
 * @author Luo Fei
 * @date 2023/07/12
 */
public final class InsightApiRequestImpl implements InsightApiRequest {

    /**
     * Insight API 的请求构建器实现
     */
    public static final class InsightApiRequestBuilderImpl implements Builder {

        private String usageType;

        private String method;

        private String url;

        private Map<String, String> pathVariableMap;

        private Map<String, String> requestParamMap;

        private Map<String, String> requestHeaderMap;

        private Object requestBody;

        private Duration timeout;

        @Override
        public Builder usageType(String usageType) {
            this.usageType = usageType;
            return this;
        }

        @Override
        public Builder method(String method) {
            this.method = method;
            return this;
        }

        @Override
        public Builder url(String url) {
            this.url = url;
            return this;
        }

        @Override
        public Builder pathVariable(Map<String, String> pathVariableMap) {
            this.pathVariableMap = pathVariableMap;
            return this;
        }

        @Override
        public Builder requestParam(Map<String, String> requestParamMap) {
            this.requestParamMap = requestParamMap;
            return this;
        }

        @Override
        public Builder requestHeader(Map<String, String> requestHeaderMap) {
            this.requestHeaderMap = requestHeaderMap;
            return this;
        }

        @Override
        public Builder requestBody(Object requestBody) {
            this.requestBody = requestBody;
            return this;
        }

        @Override
        public Builder timeout(Duration timeout) {
            this.timeout = timeout;
            return this;
        }

        @Override
        public InsightApiRequest build() {
            return new InsightApiRequestImpl(this);
        }

    }

    private final String usageType;

    private final String method;

    private final String url;

    private final Map<String, String> pathVariableMap;

    private final Map<String, String> requestParamMap;

    private final Map<String, String> requestHeaderMap;

    private final Object requestBody;

    private final Duration timeout;

    private InsightApiRequestImpl(InsightApiRequestBuilderImpl builder) {
        Assert.hasText(builder.method, "The method must have text");
        Assert.hasText(builder.url, "The url must have text");
        // usageType 默认为 QUANTITY
        this.usageType = hasText(builder.usageType) ? builder.usageType : QUANTITY_USAGE;
        this.method = builder.method;
        this.url = builder.url;
        this.pathVariableMap = builder.pathVariableMap;
        this.requestParamMap = builder.requestParamMap;
        this.requestHeaderMap = builder.requestHeaderMap;
        this.requestBody = builder.requestBody;
        this.timeout = builder.timeout;
    }

    @Override
    public String usageType() {
        return usageType;
    }

    @Override
    public String method() {
        return method;
    }

    @Override
    public String url() {
        return url;
    }

    @Override
    public Optional<Map<String, String>> pathVariable() {
        return Optional.ofNullable(pathVariableMap);
    }

    @Override
    public Optional<Map<String, String>> requestParam() {
        return Optional.ofNullable(requestParamMap);
    }

    @Override
    public Optional<Map<String, String>> requestHeader() {
        return Optional.ofNullable(requestHeaderMap);
    }

    @Override
    public Optional<Object> requestBody() {
        return Optional.ofNullable(requestBody);
    }

    @Override
    public Optional<Duration> timeout() {
        return Optional.ofNullable(timeout);
    }

}
