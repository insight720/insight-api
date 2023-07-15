package pers.project.api.gateway.filter;

import com.alibaba.fastjson2.JSON;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RSemaphore;
import org.redisson.api.RTransaction;
import org.redisson.api.RedissonClient;
import org.redisson.api.TransactionOptions;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;
import pers.project.api.common.model.Result;
import pers.project.api.common.model.dto.ClientUserInfoDTO;
import pers.project.api.common.model.dto.QuantityUsageApiInfoDTO;
import pers.project.api.common.model.query.ClientUserInfoQuery;
import pers.project.api.common.model.query.QuantityUsageApiInfoQuery;
import pers.project.api.common.util.ResultUtils;
import pers.project.api.gateway.InsightApiGatewayException;
import pers.project.api.gateway.enumaration.SignatureRequestHeaderEnum;
import pers.project.api.gateway.feign.FacadeFeignService;
import pers.project.api.gateway.feign.SecurityFeignService;
import pers.project.api.gateway.filter.factory.ProviderGatewayFilterFactory;
import pers.project.api.gateway.util.SignatureHeaderUtils;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static java.lang.Boolean.FALSE;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.util.StringUtils.hasText;
import static pers.project.api.common.constant.redis.RedisKeyPrefixConst.SIGNATURE_HEADER_NONCE_KEY_PREFIX;
import static pers.project.api.common.constant.redis.RedissonNamePrefixConst.*;
import static pers.project.api.common.enumeration.ErrorEnum.USER_REQUEST_ERROR;
import static pers.project.api.gateway.constant.ExchangeAttributeNameConst.*;
import static pers.project.api.gateway.enumaration.SignatureRequestHeaderEnum.*;
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
     * 必须在 {@link HttpLogFilter} 打印请求日志之后。
     * <p>
     * 相同顺序全局过滤器在前。
     */
    public static final int PROVIDER_GATEWAY_FILTER_ORDER = HTTP_LOG_FILTER_ORDER;

    /**
     * 时间戳超时时长（单位：毫秒）
     */
    private static final long TIMESTAMP_TIMEOUT = 60_000L;

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
     * 1. @Lazy 和 @Resource 解决循环依赖问题。
     * <p>
     * 2. Feign 服务只能异步调用。
     */
    @Lazy
    @Resource
    private SecurityFeignService securityFeignService;
    @Lazy
    @Resource
    private FacadeFeignService facadeFeignService;
    @Lazy
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Lazy
    @Resource
    private RedissonClient redissonClient;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 此过滤器在 HttpLogFilter 之后执行，可使用 Ordered 接口改变顺序
        // Pre 确定请求是否被授权
        boolean authorized;
        try {
            authorized = authorizeRequest(exchange);
        } catch (Exception e) {
            String requestId = exchange.getRequest().getId();
            log.warn("""
                    授权请求 Provider 异常
                    请求 ID: {}
                    异常信息: {}
                    """, requestId, e.getMessage());
            authorized = false;
        }
        // 未授权请求不再进入其他过滤器
        if (!authorized) {
            return exchange.getResponse().setComplete();
        }
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            // Post 根据响应状态确定执行逻辑
            Map<String, Object> attributes = exchange.getAttributes();
            // instanceof 可以同时判空，理论上一定会进入 if
            if ((attributes.get(USER_QUANTITY_USAGE_ID) instanceof String usageId)
                    && (attributes.get(API_DIGEST_ID) instanceof String digestId)) {
                RTransaction transaction = redissonClient.createTransaction(TransactionOptions.defaults());
                try {
                    addStatisticalSemaphorePermit(exchange, usageId, digestId);
                    // 提交事务
                    transaction.commit();
                } catch (Exception e) {
                    // 回滚事务
                    transaction.rollback();
                    log.error("""
                            Failed to add statistical semaphore permit, usageId: {}, digestId: {}, \
                            exception message: {}
                            """, usageId, digestId, e.getMessage());
                }
            }
        }));
    }

    /**
     * 添加统计信号量许可证
     * <p>
     * 在 Provider 响应返回后调用。也可以使用 Redis 的 String 结构实现。
     * <p>
     * 这个方法的操作应该是原子的，也就是说，它应该运行在事务中。
     *
     * @param exchange HTTP 请求-响应交互的协定
     * @param usageId  用户接口计数用法主键
     * @param digestId 接口摘要主键
     */
    private void addStatisticalSemaphorePermit(ServerWebExchange exchange, String usageId, String digestId) {
        // 增加接口调用次数统计量和用户接口调用次数统计量
        String apiTotalSemaphoreName = API_QUANTITY_USAGE_TOTAL_SEMAPHORE_NAME_PREFIX + digestId;
        redissonClient.getSemaphore(apiTotalSemaphoreName).addPermits(1);
        String userTotalSemaphoreName = USER_QUANTITY_USAGE_TOTAL_SEMAPHORE_NAME_PREFIX + usageId;
        redissonClient.getSemaphore(userTotalSemaphoreName).addPermits(1);
        HttpStatusCode statusCode = exchange.getResponse().getStatusCode();
        if (statusCode == null || !OK.isSameCodeAs(statusCode)) {
            // 增加接口失败调用次数统计量和用户接口失败调用次数统计量
            String apiFailureSemaphoreName = API_QUANTITY_USAGE_FAILURE_SEMAPHORE_NAME_PREFIX + digestId;
            redissonClient.getSemaphore(apiFailureSemaphoreName).addPermits(1);
            String userFailureSemaphoreName = USER_QUANTITY_USAGE_FAILURE_SEMAPHORE_NAME_PREFIX + usageId;
            redissonClient.getSemaphore(userFailureSemaphoreName).addPermits(1);
        }
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
    //    @SuppressWarnings("all")
    private boolean authorizeRequest(ServerWebExchange exchange) throws ExecutionException, InterruptedException {
        // 检查请求头是否缺少或重复
        ServerHttpRequest request = exchange.getRequest();
        HttpHeaders headers = request.getHeaders();
        if (hasAbsentOrDuplicateHeaders(headers)) {
            handleUnauthorizedRequest(exchange, BAD_REQUEST, "Missing or duplicate headers");
            return false;
        }
        // 请求时间不能超过 1 分钟
        long timestamp = Long.parseLong(headers.getFirst(TIMESTAMP.getHeaderName()));
        long currentTimestamp = SignatureHeaderUtils.getTimestamp();
        long leftTimeout = TIMESTAMP_TIMEOUT + timestamp - currentTimestamp;
        if (leftTimeout < 0) {
            handleUnauthorizedRequest(exchange, FORBIDDEN, "Request timeout");
            return false;
        }
        // 请求第一次到 Gateway 会添加一条 时间戳 + 随机数 的 Redis 记录，如果已经有这条记录，则请求失败
        String nonce = headers.getFirst(NONCE.getHeaderName());
        String signatureHeaderNonceKey = SIGNATURE_HEADER_NONCE_KEY_PREFIX + timestamp;
        // 这条记录过期时间为 leftTimestampTimeout 毫秒（此后时间戳检查无法通过，不需要随机数检查）
        Boolean isAbsent = redisTemplate.opsForValue().setIfAbsent(signatureHeaderNonceKey, nonce,
                leftTimeout, TimeUnit.MILLISECONDS);
        if (FALSE.equals(isAbsent)) {
            handleUnauthorizedRequest(exchange, FORBIDDEN, "Duplicate request");
            return false;
        }
        // 以下简化 CompletableFuture 和 OpenFeign 的超时时间设置，以及 CompletableFuture 的异常处理
        // 获取请求的用户数据（暂时只获取 secretKey 和 accountId，可根据需求更改）
        String secretId = headers.getFirst(SECRET_ID.getHeaderName());
        CompletableFuture<ClientUserInfoDTO> userInfoFuture = CompletableFuture.supplyAsync(() -> {
            ClientUserInfoQuery clientUserInfoQuery = new ClientUserInfoQuery();
            clientUserInfoQuery.setSecretId(secretId);
            Result<ClientUserInfoDTO> userInfoResult = securityFeignService.getClientUserInfoResult(clientUserInfoQuery);
            if (ResultUtils.isFailure(userInfoResult)) {
                throw new InsightApiGatewayException("Failed to get ClientUserInfoDTO, secretId: " + secretId);
            }
            return userInfoResult.getData();
        });
        ClientUserInfoDTO clientUserInfoDTO = userInfoFuture.get();
        // 请求的接口是否存在（需要返回 usageId 来进行信号量操作）
        CompletableFuture<QuantityUsageApiInfoDTO> apiInfoFuture = CompletableFuture.supplyAsync(() -> {
            // 入参有 accountId 和请求中需要用于验证的参数
            QuantityUsageApiInfoQuery apiInfoQuery = new QuantityUsageApiInfoQuery();
            apiInfoQuery.setAccountId(clientUserInfoDTO.getAccountId());
            apiInfoQuery.setMethod(request.getMethod().name());
            apiInfoQuery.setOriginalUrl(headers.getFirst(ORIGINAL_URL.getHeaderName()));
            Result<QuantityUsageApiInfoDTO> apiInfoResult
                    = facadeFeignService.getQuantityUsageApiInfoResult(apiInfoQuery);
            if (ResultUtils.isFailure(apiInfoResult)) {
                throw new InsightApiGatewayException
                        ("Failed to get QuantityUsageApiInfoDTO, apiInfoQuery: " + apiInfoQuery);
            }
            return apiInfoResult.getData();
        });
        // 验证请求签名是否正确
        String severCalculatedSign = SignatureHeaderUtils.getSign
                (clientUserInfoDTO.getSecretKey(), exchange);
        if (!severCalculatedSign.equals(headers.getFirst(SIGN.getHeaderName()))) {
            handleUnauthorizedRequest(exchange, FORBIDDEN, "Invalid request signature");
            return false;
        }
        QuantityUsageApiInfoDTO apiInfoDTO = apiInfoFuture.get();
        if (!hasText(apiInfoDTO.getUsageId())
                || !hasText(apiInfoDTO.getDigestId())) {
            handleUnauthorizedRequest(exchange, NOT_FOUND,
                    "API not found or user quantity usage not available");
            return false;
        }
        String usageId = apiInfoDTO.getUsageId();
        // 检查是否还有调用次数存量，并执行调用次数
        String userQuantityUsageStockSemaphoreName = USER_QUANTITY_USAGE_STOCK_SEMAPHORE_NAME_PREFIX + usageId;
        RSemaphore stockSemaphore = redissonClient.getSemaphore(userQuantityUsageStockSemaphoreName);
        boolean isAvailable = stockSemaphore.tryAcquire();
        if (!isAvailable) {
            handleUnauthorizedRequest(exchange, FORBIDDEN,
                    "API not found or user quantity usage not available");
            return false;
        }
        // 通过 Map<String, Object> attributes = exchange.getAttributes(); 添加属性以供下游使用
        Map<String, Object> attributes = exchange.getAttributes();
        attributes.put(CLIENT_ACCOUNT_ID, clientUserInfoDTO.getAccountId());
        attributes.put(USER_QUANTITY_USAGE_ID, apiInfoDTO.getUsageId());
        attributes.put(API_DIGEST_ID, apiInfoDTO.getDigestId());
        return true;
    }

    /**
     * 是否有缺少或重复的标头
     *
     * @return ture 如果有
     */
    private boolean hasAbsentOrDuplicateHeaders(HttpHeaders headers) {
        for (SignatureRequestHeaderEnum headerEnum : SignatureRequestHeaderEnum.values()) {
            List<String> header = headers.get(headerEnum.getHeaderName());
            if (CollectionUtils.isEmpty(header) || header.size() > 1) {
                return true;
            }
        }
        return false;
    }

    /**
     * 处理未经授权的请求
     *
     * @param exchange     HTTP 请求-响应交互的协定
     * @param httpStatus   响应状态码
     * @param errorMessage 错误信息
     */
    private static void handleUnauthorizedRequest(ServerWebExchange exchange, HttpStatus httpStatus, String errorMessage) {
        exchange.getResponse().setStatusCode(httpStatus);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        Result<Object> failureResult = ResultUtils.failure(USER_REQUEST_ERROR, errorMessage);
        byte[] errorBytes = JSON.toJSONString(failureResult).getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(errorBytes);
        exchange.getResponse().writeWith(Mono.just(buffer));
    }

}
