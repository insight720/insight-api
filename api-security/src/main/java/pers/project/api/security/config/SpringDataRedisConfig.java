package pers.project.api.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import pers.project.api.common.handler.CustomFastJsonRedisSerializer;

/**
 * Spring Data Redis 配置类
 *
 * @author Luo Fei
 * @date 2023/04/25
 */
@Configuration
public class SpringDataRedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        // 启用事务支持
//        template.setEnableTransactionSupport(true);
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
