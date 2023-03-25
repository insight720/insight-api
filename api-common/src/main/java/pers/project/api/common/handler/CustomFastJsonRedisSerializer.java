package pers.project.api.common.handler;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONB;
import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.filter.ContextAutoTypeBeforeHandler;
import com.alibaba.fastjson2.support.config.FastJsonConfig;
import com.alibaba.fastjson2.support.spring6.data.redis.GenericFastJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextImpl;


/**
 * 定制的 FastJson Redis 序列化器
 * <p>
 * 用于解决 Spring Security 整合 Spring Session 时存在的序列化问题。
 *
 * @author Luo Fei
 * @date 2023/3/19
 * @see GenericFastJsonRedisSerializer
 */
@SuppressWarnings("unused")
public class CustomFastJsonRedisSerializer implements RedisSerializer<Object> {

    private final FastJsonConfig config = new FastJsonConfig();

    public CustomFastJsonRedisSerializer() {
        // 相较于 GenericFastJsonRedisSerializer，添加了 Feature.FieldBased
        config.setReaderFeatures(JSONReader.Feature.SupportAutoType, JSONReader.Feature.FieldBased);
        config.setWriterFeatures(JSONWriter.Feature.WriteClassName, JSONWriter.Feature.FieldBased);
    }

    public CustomFastJsonRedisSerializer(String[] acceptNames, boolean jsonb) {
        this();
        config.setReaderFilters(new ContextAutoTypeBeforeHandler(acceptNames));
        config.setJSONB(jsonb);
    }

    public CustomFastJsonRedisSerializer(String[] acceptNames) {
        this(acceptNames, false);
    }

    public CustomFastJsonRedisSerializer(boolean jsonb) {
        this(new String[0], jsonb);
    }

    @Override
    public byte[] serialize(Object object) throws SerializationException {
        // SecurityContextImpl 无法序列化，只序列化 Authentication
        if (object instanceof SecurityContextImpl securityContext) {
            object = securityContext.getAuthentication();
        }
        if (object == null) {
            return new byte[0];
        }
        try {
            if (config.isJSONB()) {
                return JSONB.toBytes(object, config.getWriterFeatures());
            } else {
                return JSON.toJSONBytes(object, config.getWriterFeatures());
            }
        } catch (Exception ex) {
            throw new SerializationException("Could not serialize: " + ex.getMessage(), ex);
        }
    }

    @Override
    public Object deserialize(byte[] bytes) throws SerializationException {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        Object object;
        try {
            if (config.isJSONB()) {
                object = JSONB.parseObject(bytes, Object.class, null, config.getReaderFilters(), config.getReaderFeatures());
            } else {
                object = JSON.parseObject(bytes, Object.class, null, config.getReaderFilters(), config.getReaderFeatures());
            }
        } catch (Exception ex) {
            throw new SerializationException("Could not deserialize: " + ex.getMessage(), ex);
        }
        // Authentication 反序列化时还原为 SecurityContextImpl
        if (object instanceof Authentication authentication) {
            SecurityContextImpl securityContext = new SecurityContextImpl();
            securityContext.setAuthentication(authentication);
            return securityContext;
        }
        return object;
    }

}

