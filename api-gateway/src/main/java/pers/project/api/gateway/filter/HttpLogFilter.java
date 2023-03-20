package pers.project.api.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.NettyWriteResponseFilter;
import org.springframework.cloud.gateway.filter.factory.CacheRequestBodyGatewayFilterFactory;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebSession;
import pers.project.api.common.constant.UserConst;
import pers.project.api.common.model.entity.UserEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;

import static io.netty.util.CharsetUtil.UTF_8;
import static org.springframework.cloud.gateway.filter.NettyWriteResponseFilter.WRITE_RESPONSE_FILTER_ORDER;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.CACHED_REQUEST_BODY_ATTR;
import static org.springframework.core.NestedExceptionUtils.getMostSpecificCause;

/**
 * HTTP 日志过滤器
 *
 * @author Luo Fei
 * @version 2023/3/10
 */
@Slf4j
@Component
public class HttpLogFilter implements GlobalFilter, Ordered {

    /**
     * 过滤器顺序
     * <p>
     * 必须在 {@link NettyWriteResponseFilter} 写入响应结果之前。
     */
    public static final int HTTP_LOG_FILTER_ORDER = WRITE_RESPONSE_FILTER_ORDER - 1;

    /**
     * HTTP 请求日志格式
     */
    private static final String HTTP_REQUEST_LOG_FORMAT = """
            REQUEST_LOG
            User ID: {}
            Request ID: {}
            Method: {}
            URI: {}
            Path: {}
            Params: {}
            Body: {}
            Headers: {}
            Cookies: {}
            LocalAddress: {}
            RemoteAddress: {}
            SslInfo: {}
            """;

    /**
     * HTTP 响应日志格式
     */
    private static final String HTTP_RESPONSE_LOG_FORMAT = """
            RESPONSE_LOG
            Request ID: {}
            StatusCode: {}
            Headers: {}
            Cookies: {}
            Body: {}
            """;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        logHttpRequest(exchange);
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        if (requireHttpResponseBodyLog(exchange)) {
            response = decorateHttpResponse(exchange);
        } else {
            logHttpResponse(response, request.getId(), null);
        }
        return chain.filter(exchange.mutate().response(response).build());
    }

    @Override
    public int getOrder() {
        return HTTP_LOG_FILTER_ORDER;
    }

    /**
     * 日志记录 HTTP 请求
     */
    private void logHttpRequest(ServerWebExchange exchange) {
        Object[] logArgs = extractRequestLogArgs(exchange);
        log.info(HTTP_REQUEST_LOG_FORMAT, logArgs);
    }

    /**
     * 提取请求日志参数
     */
    private Object[] extractRequestLogArgs(ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();
        return new Object[]{
                getUserId(exchange),
                request.getId(),
                request.getMethod(),
                request.getURI(),
                request.getPath(),
                request.getQueryParams(),
                getHttpRequestBody(exchange),
                request.getHeaders(),
                request.getCookies(),
                request.getLocalAddress(),
                request.getRemoteAddress(),
                request.getSslInfo(),
        };
    }

    /**
     * 获取用户 ID
     *
     * @return 用户 ID，可能为 null。
     */
    private Long getUserId(ServerWebExchange exchange) {
        CompletableFuture<WebSession> webSessionFuture = exchange.getSession().toFuture();
        UserEntity userEntity = null;
        try {
            userEntity = webSessionFuture.get()
                    .getAttribute(UserConst.USER_LOGIN_STATE);
        } catch (Exception e) {
            String requestId = exchange.getRequest().getId();
            log.warn("""
                    获取用户 ID 异常
                    请求 ID: {}
                    信息: {}
                    """, requestId, getMostSpecificCause(e));
        }
        return userEntity == null ? null : userEntity.getId();
    }

    /**
     * 获取 HTTP 请求正文
     * <p>
     * 使用了框架自带的 {@link CacheRequestBodyGatewayFilterFactory }，
     * 如果有缓存限制或特殊需求可以仿照源码自定义过滤器。
     *
     * @return 缓存的请求体，可能为 null。一般是 String 类型，可以通过配置文件指定。
     * @see <a href="https://springdoc.cn/spring-cloud-gateway/#cacherequestbody">
     * CacheRequestBodyGatewayFilterFactory</a>
     */
    private Object getHttpRequestBody(ServerWebExchange exchange) {
        return exchange.getAttribute(CACHED_REQUEST_BODY_ATTR);
    }

    /**
     * 是否需要 HTTP 响应体日志
     * <p>
     * 可以根据需求进一步修改此方法。
     *
     * @return true 如果需要
     */
    private boolean requireHttpResponseBodyLog(ServerWebExchange exchange) {
        HttpStatusCode httpStatusCode = exchange.getResponse().getStatusCode();
        if (httpStatusCode == null) {
            return false;
        }
        return HttpStatus.OK.isSameCodeAs(httpStatusCode);
    }

    /**
     * 装饰 HTTP 响应
     * <p>
     * 增加日志记录响应体的功能。
     */
    private ServerHttpResponse decorateHttpResponse(ServerWebExchange exchange) {
        return new HttpResponseLogDecorator
                (exchange.getResponse(), exchange.getRequest().getId());
    }

    /**
     * 日志记录 HTTP 响应
     */
    private static void logHttpResponse(ServerHttpResponse response,
                                        String requestID,
                                        String responseBody) {
        Object[] logArgs = extractHttpResponseLogArgs(response, requestID, responseBody);
        log.info(HTTP_RESPONSE_LOG_FORMAT, logArgs);
    }

    /**
     * 提取 HTTP 响应日志参数
     */
    private static Object[] extractHttpResponseLogArgs(ServerHttpResponse response,
                                                       String requestID,
                                                       String responseBody) {
        return new Object[]{
                requestID,
                response.getStatusCode(),
                response.getHeaders(),
                response.getCookies(),
                responseBody,
        };
    }

    /**
     * HTTP 响应日志装饰器
     * <p>
     * 增加日志记录响应体的功能。
     */
    private static class HttpResponseLogDecorator extends ServerHttpResponseDecorator {

        /**
         * 请求 ID
         * <p>
         * 可以和请求日志形成映射。
         */
        private final String requestId;

        public HttpResponseLogDecorator(ServerHttpResponse delegate, String requestId) {
            super(delegate);
            this.requestId = requestId;
        }

        @Override
        // 抑制 null 警告
        @SuppressWarnings("all")
        public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
            // 在响应结果返回 Gateway 后执行
            Flux<DataBuffer> bodyFlux = Flux.from(body)
                    .map(dataBuffer -> {
                        // 安全起见，使用堆内存而不是直接内存
                        ByteBuffer byteBuffer = ByteBuffer.allocate
                                (dataBuffer.readableByteCount());
                        dataBuffer.toByteBuffer(byteBuffer);
                        ServerHttpResponse delegate = getDelegate();
                        // 使用 asReadOnlyBuffer()，否则无法返回响应
                        String responseBody = UTF_8.decode
                                (byteBuffer.asReadOnlyBuffer()).toString();
                        logHttpResponse(delegate, requestId, responseBody);
                        return delegate.bufferFactory().wrap(byteBuffer);
                    });
            return super.writeWith(bodyFlux);
        }

    }

}


