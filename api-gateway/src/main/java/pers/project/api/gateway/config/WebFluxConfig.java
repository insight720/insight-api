package pers.project.api.gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.autoconfigure.web.reactive.WebSessionIdResolverAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpCookie;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.session.web.http.DefaultCookieSerializer;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.session.CookieWebSessionIdResolver;
import org.springframework.web.server.session.WebSessionIdResolver;

import java.util.Base64;
import java.util.Collections;
import java.util.List;

/**
 * WebFlux 配置类
 * <p>
 * 这个类的部分内容之前被用于测试开发目的，可能会被弃用，暂未测试删除后是否有影响。。
 *
 * @author Luo Fei
 * @date 2023/07/17
 * @deprecated 可能会在之后删除。
 */
@Slf4j
@SuppressWarnings("all") // Suppress all warnings
@Deprecated(forRemoval = true)
@Configuration
public class WebFluxConfig implements WebFluxConfigurer {

    @Override
    public void configureHttpMessageCodecs(ServerCodecConfigurer configurer) {
        configurer.defaultCodecs().maxInMemorySize(1024 * 10);
    }

    /**
     * 解决 WebFlux 应用中缺少 {@code HttpMessageConverters} 的问题。
     *
     * @see HttpMessageConvertersAutoConfiguration#messageConverters(ObjectProvider)
     */
    @Bean
    @ConditionalOnMissingBean
    public HttpMessageConverters messageConverters(ObjectProvider<HttpMessageConverter<?>> converters) {
        return new HttpMessageConverters(converters.orderedStream().toList());
    }

    /**
     * 覆盖 WebSessionIdResolver 的默认实现。
     *
     * @see WebSessionIdResolverAutoConfiguration#webSessionIdResolver()
     */
    @Bean
    public WebSessionIdResolver webSessionIdResolver() {
        return new CustomWebSessionIdResolver();
    }

    /**
     * 自定义 WebSessionId 解析程序
     */
    private static class CustomWebSessionIdResolver extends CookieWebSessionIdResolver {

        /**
         * 改写 WebFlux 中解析 WebSessionId 的策略，将 Session 信息进行 Base64 解码。
         * <p>
         * 默认实现中没有 Base64 解码，SessionId 传到 Gateway 下游时不一致，导致 Session 不共享。
         *
         * @see DefaultCookieSerializer#setUseBase64Encoding(boolean)
         */
        @Override
        public List<String> resolveSessionIds(ServerWebExchange exchange) {
            MultiValueMap<String, HttpCookie> cookieMap = exchange.getRequest().getCookies();
            List<HttpCookie> cookies = cookieMap.get(getCookieName());
            if (cookies == null) {
                return Collections.emptyList();
            }
            return cookies.stream().map(httpCookie -> {
                String value = httpCookie.getValue();
                String sessionId = base64Decode(value);
                return sessionId != null ? sessionId : value;
            }).toList();
        }

        private String base64Decode(String base64Value) {
            try {
                byte[] decodedCookieBytes = Base64.getDecoder().decode(base64Value);
                return new String(decodedCookieBytes);
            } catch (Exception ex) {
                log.error("Unable to Base64 decode value: " + base64Value);
                return null;
            }
        }

    }

}
