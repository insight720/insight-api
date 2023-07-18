package pers.project.api.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 缓存请求体过滤器
 *
 * @author Luo Fei
 * @date 2023/07/16
 */
@Component
public class CacheRequestBodyFilter implements Ordered, GatewayFilter, GlobalFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return ServerWebExchangeUtils
                .cacheRequestBody(
                        exchange,
                        (serverHttpRequest) -> chain.filter(
                                exchange.mutate().request(serverHttpRequest).build()));
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

}

