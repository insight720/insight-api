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
 * 请求体缓存设置
 * <p>
 * 这个类先前被用于解决 Gateway 框架的请求体缓存问题，
 * 更换请求体缓存方式后没有删除，暂未测试删除后是否有影响。
 *
 * @author Luo Fei
 * @date 2023/07/17
 * @deprecated 可能会在之后测试并删除。
 */
@Deprecated(forRemoval = true)
@Configuration
@SuppressWarnings("all") // Suppress all warnings
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