package pers.project.api.gateway.filter;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.CacheRequestBodyGatewayFilterFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;
import pers.project.api.gateway.common.BaseResponse;
import pers.project.api.gateway.common.ResultUtils;
import pers.project.api.gateway.enums.RequestHeadersEnum;
import pers.project.api.gateway.feign.FacadeFeignService;
import pers.project.api.gateway.feign.SecurityFeignService;
import pers.project.api.gateway.filter.factory.ProviderGatewayFilterFactory;
import pers.project.api.gateway.model.ApiInfo;
import pers.project.api.gateway.model.User;
import pers.project.api.gateway.utils.SignUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static cn.hutool.core.exceptions.ExceptionUtil.getRootCause;
import static cn.hutool.core.exceptions.ExceptionUtil.stacktraceToString;
import static io.netty.util.CharsetUtil.UTF_8;
import static org.springframework.cloud.gateway.filter.NettyWriteResponseFilter.WRITE_RESPONSE_FILTER_ORDER;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.CACHED_REQUEST_BODY_ATTR;
import static pers.project.api.gateway.enums.RequestHeadersEnum.*;

/**
 * Provider 网关过滤器
 *
 * @author Luo Fei
 * @date 2023/3/10
 */
@Slf4j
@Component
public class ProviderGatewayFilter implements GatewayFilter, Ordered {

    /**
     * 过滤器顺序
     * <p>
     * 必须在 NettyWriteResponseFilter 写入响应结果之前。
     */
    public static final int PROVIDER_GATEWAY_FILTER_ORDER = WRITE_RESPONSE_FILTER_ORDER - 1;

    /**
     * 请求日志格式
     */
    private static final String REQUEST_LOG_FORMAT = """
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
            SslInfo:{}
            """;

    /**
     * 响应日志格式
     */
    private static final String RESPONSE_LOG_FORMAT = """
            RESPONSE_LOG
            RequestID:{}
            StatusCode: {}
            Headers: {}
            Cookies: {}
            Body: {}
            """;

    /**
     * 用户信息的线程本地变量
     * <p>
     * 如果存储的信息过多，可以更换存储的类型，减少数据量。
     */
    private static final ThreadLocal<User> USER_THREAD_LOCAL = new ThreadLocal<>();

    /**
     * 配置属性
     */
    @Resource
    private ProviderGatewayFilterFactory.Config config;

    /**
     * 注意:
     * <p>
     * 1. @Lazy 解决循环依赖问题。
     * <p>
     * 2. Feign 服务只能异步调用。
     */
    @Lazy
    @Resource
    private SecurityFeignService securityFeignService;
    @Lazy
    @Resource
    private FacadeFeignService facadeFeignService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 确定请求是否被授权
        boolean authorized;
        try {
            authorized = authorizeRequest(exchange);
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            log.error(stacktraceToString(getRootCause(e)));
            authorized = false;
        }
        // 未授权请求不再进入其他过滤器
        if (!authorized) {
            return handleUnauthorizedRequest(exchange);
        }
        ServerHttpRequest mutatedRequest;
        ServerHttpResponse mutatedResponse;
        try {
            // 处理授权请求
            mutatedRequest = handleAuthorizedRequest(exchange);
        } finally {
            USER_THREAD_LOCAL.remove();
        }
        // 处理原始响应
        mutatedResponse = mutateResponseIfNecessary(exchange);
        return chain.filter(exchange.mutate()
                .request(mutatedRequest)
                .response(mutatedResponse)
                .build());
    }

    @Override
    public int getOrder() {
        return PROVIDER_GATEWAY_FILTER_ORDER;
    }

    /**
     * 授权请求
     *
     * @return true 如果请求被授权
     */
    @SuppressWarnings("all") // 抑制不必要的 NPE 警告
    private boolean authorizeRequest(ServerWebExchange exchange)
            throws ExecutionException, InterruptedException, TimeoutException {
        // 检查请求头是否缺少或重复
        ServerHttpRequest request = exchange.getRequest();
        HttpHeaders headers = request.getHeaders();
        if (hasAbsentOrDuplicateHeaders(headers)) {
            return false;
        }
        // 请求时间不能超过 1 分钟
        long timestamp = Long.parseLong(headers.getFirst(TIMESTAMP.getName()));
        long currentTime = System.currentTimeMillis() / 1000;
        final long ONE_MINUTE = 60L;
        if (currentTime - timestamp > ONE_MINUTE) {
            return false;
        }
        // 获取请求的用户数据
        CompletableFuture<BaseResponse<User>> userFuture
                = CompletableFuture.supplyAsync
                (() -> {
                    String accessKey = headers.getFirst(ACCESS_KEY.getName());
                    return securityFeignService.getInvokeUser(accessKey);
                });
        BaseResponse<User> userResponse = ResultUtils.getFromFuture(userFuture);
        if (ResultUtils.isError(userResponse)) {
            return false;
        }
        User user = userResponse.getData();
        USER_THREAD_LOCAL.set(user);
        // 检查签名是否合法
        String sign = headers.getFirst(SIGN.getName());
        String body = headers.getFirst(BODY.getName());
        String secretKey = user.getSecretKey();
        String serverSign = SignUtils.genSign(body, secretKey);
        if (!sign.equals(serverSign)) {
            return false;
        }
        // 4. 请求的模拟接口是否存在，以及请求方法是否匹配
        CompletableFuture<BaseResponse<ApiInfo>> apiInfoFuture
                = CompletableFuture.supplyAsync(() -> {
            String path = request.getPath().value();
            String method = request.getMethod().name();
            return facadeFeignService.getApiInfo(path, method);
        });
        BaseResponse<ApiInfo> apiInfoResponse = ResultUtils.getFromFuture(apiInfoFuture);
        if (ResultUtils.isError(apiInfoResponse)) {
            return false;
        }
        ApiInfo apiInfo = apiInfoResponse.getData();
        // todo 是否还有调用次数
        return true;
    }

    /**
     * 是否有缺少或重复的标头
     *
     * @return ture 如果有
     */
    private boolean hasAbsentOrDuplicateHeaders(HttpHeaders headers) {
        for (RequestHeadersEnum headerEnum : RequestHeadersEnum.values()) {
            List<String> header = headers.get(headerEnum.getName());
            if (CollectionUtils.isEmpty(header)
                    || header.size() > 1) {
                return true;
            }
        }
        return false;
    }

    /**
     * 处理未经授权的请求
     */
    private Mono<Void> handleUnauthorizedRequest(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.FORBIDDEN);
        return response.setComplete();
    }

    /**
     * 处理授权请求
     */
    private ServerHttpRequest handleAuthorizedRequest(ServerWebExchange exchange) {
        logSeverHttpRequest(exchange);
        return mutateRequestIfNecessary(exchange.getRequest());
    }

    /**
     * 日志记录请求内容
     */
    private void logSeverHttpRequest(ServerWebExchange exchange) {
        if (log.isInfoEnabled()) {
            Object[] logArguments = extractRequestLogArguments(exchange);
            log.info(REQUEST_LOG_FORMAT, logArguments);
        }
    }

    /**
     * 提取请求日志参数
     */
    private Object[] extractRequestLogArguments(ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();
        return new Object[]{
                USER_THREAD_LOCAL.get().getId(),
                request.getId(),
                request.getMethod(),
                request.getURI(),
                request.getPath(),
                request.getQueryParams(),
                readRequestBody(exchange),
                request.getHeaders(),
                request.getCookies(),
                request.getLocalAddress(),
                request.getRemoteAddress(),
                request.getSslInfo(),
        };
    }

    /**
     * 读取请求体
     * <p>
     * 使用了框架自带的 {@link CacheRequestBodyGatewayFilterFactory }，
     * 如果有缓存限制或特殊需求可以仿照源码自定义过滤器。
     *
     * @see <a href="https://springdoc.cn/spring-cloud-gateway/#cacherequestbody">
     * CacheRequestBodyGatewayFilterFactory</a>
     */
    private String readRequestBody(ServerWebExchange exchange) {
        return exchange.getAttribute(CACHED_REQUEST_BODY_ATTR);
    }

    /**
     * 必要时修改请求
     */
    private ServerHttpRequest mutateRequestIfNecessary(ServerHttpRequest request) {
        ServerHttpRequest.Builder builder = request.mutate();
        // 此处可以修改请求
        return builder.build();
    }

    /**
     * 必要时修改响应
     */
    private ServerHttpResponse mutateResponseIfNecessary(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        // 此处可以修改响应
        if (HttpStatus.OK.equals(response.getStatusCode())) {
            // 响应为 OK 则日志记录响应内容，可以根据需求更改
            return new ProviderResponseDecorator(response, exchange.getRequest().getId());
        }
        return response;
    }

    /**
     * Provider 响应修饰器
     * <p>
     * 用于输出响应日志。
     */
    private static class ProviderResponseDecorator extends ServerHttpResponseDecorator {

        /**
         * 请求 ID
         * <p>
         * 可以和请求日志形成映射。
         */
        private final String requestId;

        public ProviderResponseDecorator(ServerHttpResponse delegate, String requestId) {
            super(delegate);
            this.requestId = requestId;
        }

        /**
         * 在响应结果返回后执行
         */
        @Override
        public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
            Flux<DataBuffer> bodyFlux = Flux.from(body)
                    .map(dataBuffer -> {
                        // 安全起见，使用堆内存而不是直接内存
                        ByteBuffer byteBuffer = ByteBuffer.allocate
                                (dataBuffer.readableByteCount());
                        dataBuffer.toByteBuffer(byteBuffer);
                        ServerHttpResponse delegate = getDelegate();
                        // 必须调用 asReadOnlyBuffer()，否则无法返回响应
                        String responseBody = UTF_8.decode
                                (byteBuffer.asReadOnlyBuffer()).toString();
                        logSeverHttpResponse(delegate, requestId, responseBody);
                        return delegate.bufferFactory().wrap(byteBuffer);
                    });
            return super.writeWith(bodyFlux);
        }

    }

    /**
     * 日志记录响应
     */
    private static void logSeverHttpResponse(ServerHttpResponse response,
                                             String requestID,
                                             String body) {
        if (log.isInfoEnabled()) {
            Object[] logArguments = extractResponseLogArguments(response, requestID, body);
            log.info(RESPONSE_LOG_FORMAT, logArguments);
        }
    }

    /**
     * 提取响应日志参数
     */
    private static Object[] extractResponseLogArguments(ServerHttpResponse response,
                                                        String requestID,
                                                        String body) {
        return new Object[]{
                requestID,
                response.getStatusCode(),
                response.getHeaders(),
                response.getCookies(),
                body,
        };
    }

}


