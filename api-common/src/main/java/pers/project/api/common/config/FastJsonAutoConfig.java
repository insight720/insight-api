package pers.project.api.common.config;

import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.support.config.FastJsonConfig;
import com.alibaba.fastjson2.support.spring6.http.converter.FastJsonHttpMessageConverter;
import com.alibaba.fastjson2.support.spring6.webservlet.view.FastJsonJsonView;
import jakarta.annotation.Resource;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

/**
 * FastJson 自动配置类
 *
 * @author Luo Fei
 * @version 2023/3/15
 * @see <a href="https://github.com/alibaba/fastjson2/blob/main/docs/spring_support_cn.md">FastJson2 配置</a>
 */
@AutoConfiguration
public class FastJsonAutoConfig {

    @Bean
    @ConditionalOnMissingBean
    public FastJsonConfig fastJsonConfig() {
        FastJsonConfig config = new FastJsonConfig();
        config.setReaderFeatures(JSONReader.Feature.FieldBased);
        config.setWriterFeatures(JSONWriter.Feature.FieldBased);
        return config;
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnWebApplication(type = Type.SERVLET)
    public static class WebMvcConfig implements WebMvcConfigurer {

        @Resource
        private FastJsonConfig fastJsonConfig;

        @Override
        public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
            FastJsonHttpMessageConverter converter = new FastJsonHttpMessageConverter();
            converter.setDefaultCharset(StandardCharsets.UTF_8);
            converter.setSupportedMediaTypes
                    (Collections.singletonList(MediaType.APPLICATION_JSON));
            converter.setFastJsonConfig(fastJsonConfig);
            converters.add(0, converter);
        }

        @Override
        public void configureViewResolvers(ViewResolverRegistry registry) {
            FastJsonJsonView fastJsonJsonView = new FastJsonJsonView();
            fastJsonJsonView.setFastJsonConfig(fastJsonConfig);
            registry.enableContentNegotiation(fastJsonJsonView);
        }

    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnWebApplication(type = Type.REACTIVE)
    public static class WebFluxConfig {

        @Resource
        private FastJsonConfig fastJsonConfig;

        @Bean
        @Order(Ordered.HIGHEST_PRECEDENCE)
        @ConditionalOnMissingBean
        public FastJsonHttpMessageConverter fastJsonHttpMessageConverter() {
            FastJsonHttpMessageConverter converter = new FastJsonHttpMessageConverter();
            converter.setDefaultCharset(StandardCharsets.UTF_8);
            converter.setSupportedMediaTypes
                    (Collections.singletonList(MediaType.APPLICATION_JSON));
            converter.setFastJsonConfig(fastJsonConfig);
            return converter;
        }

    }

}
