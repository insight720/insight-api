package pers.project.api.common.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.session.FlushMode;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisIndexedHttpSession;
import org.springframework.session.data.redis.config.annotation.web.server.EnableRedisWebSession;
import pers.project.api.common.handler.CustomFastJsonRedisSerializer;

/**
 * Session 自动配置类
 *
 * @author Luo Fei
 * @date 2023/03/14
 */
@Slf4j
@AutoConfiguration
public class SessionAutoConfig {

    @Bean
    public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
        return new CustomFastJsonRedisSerializer(new String[]{
                "org.springframework.security.core.context.SecurityContextImpl",
                "org.springframework.security.authentication.UsernamePasswordAuthenticationToken",
                "org.springframework.security.web.authentication.WebAuthenticationDetails",
                "pers.project.api.security.handler.CustomizedGrantedAuthority",
                "pers.project.api.security.model.CustomUserDetails"
        });
    }

    // TODO: 2023/6/9 必须在配置文件中指明 spring.session.repository-type=indexed，否则会有两个 Bean
    @EnableRedisIndexedHttpSession(flushMode = FlushMode.IMMEDIATE)
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnWebApplication(type = Type.SERVLET)
    public static class HttpSessionConfig {
        {

            System.out.println("HttpSessionConfig.实例初始值设定项");
        }
    }

    @EnableRedisWebSession
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnWebApplication(type = Type.REACTIVE)
    public static class WebSessionConfig {
        {
            System.out.println("WebSessionConfig.实例初始值设定项");
        }
    }

}

