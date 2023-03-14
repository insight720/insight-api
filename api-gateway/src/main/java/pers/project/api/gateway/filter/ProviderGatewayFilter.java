package pers.project.api.gateway.filter;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;
import pers.project.api.common.model.dto.response.BaseResponse;
import pers.project.api.common.model.entity.ApiInfo;
import pers.project.api.common.model.entity.User;
import pers.project.api.common.util.ResultUtils;
import pers.project.api.common.util.SignUtils;
import pers.project.api.gateway.enums.RequestHeadersEnum;
import pers.project.api.gateway.feign.FacadeFeignService;
import pers.project.api.gateway.feign.SecurityFeignService;
import pers.project.api.gateway.filter.factory.ProviderGatewayFilterFactory;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static pers.project.api.gateway.enums.RequestHeadersEnum.*;
import static pers.project.api.gateway.filter.HttpLogFilter.HTTP_LOG_FILTER_ORDER;

/**
 * Provider 网关过滤器
 *
 * @author Luo Fei
 * @date 2023/3/13
 */
@Slf4j
@Component
public class ProviderGatewayFilter implements GatewayFilter, Ordered {

    /**
     * 过滤器顺序
     * <p>
     * 必须在全局过滤器之前。
     */
    public static final int PROVIDER_GATEWAY_FILTER_ORDER = HTTP_LOG_FILTER_ORDER;

    /**
     * 配置属性
     * <p>
     * 可以通过配置属性进行控制。
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
        } catch (Exception e) {
            log.error("ProviderGatewayFilter.authorizeRequest", e);
            authorized = false;
        }
        // 未授权请求不再进入其他过滤器
        if (!authorized) {
            return handleUnauthorizedRequest(exchange);
        }
        return chain.filter(exchange);
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
    private boolean authorizeRequest(ServerWebExchange exchange) throws Exception {
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
        BaseResponse<User> userResponse = ResultUtils.futureGet(userFuture);
        if (ResultUtils.isFailure(userResponse)) {
            return false;
        }
        User user = userResponse.getData();
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
        BaseResponse<ApiInfo> apiInfoResponse = ResultUtils.futureGet(apiInfoFuture);
        if (ResultUtils.isFailure(apiInfoResponse)) {
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


}
