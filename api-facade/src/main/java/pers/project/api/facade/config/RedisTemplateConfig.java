package pers.project.api.facade.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import pers.project.api.common.handler.CustomFastJsonRedisSerializer;

/**
 * {@code RedisTemplate} 配置类
 * <p>
 * 放置在 api-common 中与 {@link RedisAutoConfiguration#redisTemplate(RedisConnectionFactory)} 的自动配置有冲突。
 * <p>
 * 配置两个 {@code RedisTemplate}，一个支持事务，另外一个不支持，
 * 以解决支持事务的 {@code RedisTemplate} 返回值为 {@code null} 的问题。
 *
 * @author Luo Fei
 * @date 2023/06/30
 */
@Configuration
@RequiredArgsConstructor
public class RedisTemplateConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = getBasicRedisTemplate(connectionFactory);
        // 关闭事务支持
        template.setEnableTransactionSupport(false);
        return template;
    }

    @Bean
    @Lazy
    public RedisTemplate<String, Object> redisTransactionTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = getBasicRedisTemplate(connectionFactory);
        // 启用事务支持
        template.setEnableTransactionSupport(true);
        return template;
    }

    private static RedisTemplate<String, Object> getBasicRedisTemplate
            (RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        // key 用 String 序列化
        template.setKeySerializer(RedisSerializer.string());
        template.setHashKeySerializer(RedisSerializer.string());
        // value 用 FastJson2 序列化
        CustomFastJsonRedisSerializer jsonSerializer = new CustomFastJsonRedisSerializer();
        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);
        return template;
    }

}
