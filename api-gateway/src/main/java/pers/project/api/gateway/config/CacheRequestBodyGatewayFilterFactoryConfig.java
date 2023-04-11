package pers.project.api.gateway.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.web.codec.CodecCustomizer;
import org.springframework.cloud.gateway.config.GatewayAutoConfiguration;
import org.springframework.cloud.gateway.filter.factory.CacheRequestBodyGatewayFilterFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.support.DefaultServerCodecConfigurer;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;

/**
 * @author Luo Fei
 * @date 2023/03/29
 */
@Configuration
@AutoConfigureAfter({GatewayAutoConfiguration.class})
public class CacheRequestBodyGatewayFilterFactoryConfig {

    @Autowired
    private CodecCustomizer defaultCodecCustomizer;

    @Bean
    public BeanPostProcessor cacheRequestBodyGatewayFilterFactoryBeanPostProcessor() {
        DefaultServerCodecConfigurer serverCodecConfigurer = new DefaultServerCodecConfigurer();
        defaultCodecCustomizer.customize(serverCodecConfigurer);
        return new BeanPostProcessor() {
            @Override
            public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
                if (!(bean instanceof CacheRequestBodyGatewayFilterFactory)) {
                    return bean;
                }
                // use java reflection to replace messageReaders in CacheRequestBodyGatewayFilterFactory
                // with message readers configured from CodecConfigurer
                CacheRequestBodyGatewayFilterFactory cacheFactoryBean = (CacheRequestBodyGatewayFilterFactory) bean;
                Field messageReadersField = ReflectionUtils.findField(CacheRequestBodyGatewayFilterFactory.class, "messageReaders");
                messageReadersField.setAccessible(true);
                ReflectionUtils.setField(messageReadersField, cacheFactoryBean, serverCodecConfigurer.getReaders());
                messageReadersField.setAccessible(false);
                return cacheFactoryBean;
            }
        };
    }

}