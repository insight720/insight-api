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
import org.springframework.web.server.WebSession;
import pers.project.api.common.constant.UserConst;
import pers.project.api.common.model.Response;
import pers.project.api.common.model.entity.ApiInfoEntity;
import pers.project.api.common.model.entity.UserEntity;
import pers.project.api.common.util.ResponseUtils;
import pers.project.api.common.util.SignUtils;
import pers.project.api.gateway.constant.enumeration.GatewayHeaderEnum;
import pers.project.api.gateway.feign.FacadeFeignService;
import pers.project.api.gateway.feign.SecurityFeignService;
import pers.project.api.gateway.filter.factory.ProviderGatewayFilterFactory;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.springframework.core.NestedExceptionUtils.getMostSpecificCause;
import static pers.project.api.gateway.constant.enumeration.GatewayHeaderEnum.*;
import static pers.project.api.gateway.filter.HttpLogFilter.HTTP_LOG_FILTER_ORDER;

/**
 * Provider 网关过滤器
 *
 * @author Luo Fei
 * @date 2023/03/13
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
            String requestId = exchange.getRequest().getId();
            log.warn("""
                    授权请求 Provider 异常
                    请求 ID: {}
                    信息: {}
                    """, requestId, getMostSpecificCause(e));
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
    // 抑制 null 警告
    @SuppressWarnings("all")
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
        UserEntity userEntity = getUserInfo(exchange);
        if (userEntity == null) {
            return false;
        }
        // 检查签名是否合法
        String sign = headers.getFirst(SIGN.getName());
        String body = headers.getFirst(BODY.getName());
        String secretKey = userEntity.getSecretKey();
        String serverSign = SignUtils.sign(body, secretKey);
        if (!sign.equals(serverSign)) {
            return false;
        }
        String method = request.getMethod().name();
        // 4. 请求的模拟接口是否存在，以及请求方法是否匹配
        CompletableFuture<Response<ApiInfoEntity>> apiInfoFuture = CompletableFuture.supplyAsync(() -> {
            String path = request.getPath().value();
            return facadeFeignService.getApiInfo(path, method);
        });
        Response<ApiInfoEntity> apiInfoResponse = apiInfoFuture.get();
        if (ResponseUtils.isFailure(apiInfoResponse)) {
            return false;
        }
        ApiInfoEntity apiInfoEntity = apiInfoResponse.getData();
        // todo 是否还有调用次数
        return true;
    }

    /**
     * 获取用户信息
     * <p>
     * 从 WebSession 或者 Security 服务获取。
     *
     * @return 用户信息，可能为 null。
     */
    private UserEntity getUserInfo(ServerWebExchange exchange) throws Exception {
        // 从 WebSession 获取用户信息
        CompletableFuture<WebSession> webSessionFuture = exchange.getSession().toFuture();
        UserEntity userEntity = webSessionFuture.get().getAttribute(UserConst.USER_LOGIN_STATE);
        if (userEntity != null) {
            return userEntity;
        }
        // 从 Security 服务获取用户信息
        CompletableFuture<Response<UserEntity>> userFuture = CompletableFuture.supplyAsync(() -> {
            String accessKey = exchange.getRequest().getHeaders().getFirst(ACCESS_KEY.getName());
            return securityFeignService.getInvokeUser(accessKey);
        });
        Response<UserEntity> userResponse = userFuture.get();
        if (ResponseUtils.isFailure(userResponse)) {
            return null;
        }
        return userResponse.getData();
    }

    /**
     * 是否有缺少或重复的标头
     *
     * @return ture 如果有
     */
    private boolean hasAbsentOrDuplicateHeaders(HttpHeaders headers) {
        for (GatewayHeaderEnum headerEnum : GatewayHeaderEnum.values()) {
            List<String> header = headers.get(headerEnum.getName());
            if (CollectionUtils.isEmpty(header) || header.size() > 1) {
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
