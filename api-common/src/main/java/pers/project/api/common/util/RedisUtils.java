package pers.project.api.common.util;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import pers.project.api.common.constant.redis.RedisScriptConst;

import java.util.Collections;

import static pers.project.api.common.constant.redis.RedisScriptConst.IDEMPOTENCY_TOKEN_LUA_SCRIPT;

/**
 * Redis 工具类
 *
 * @author Luo Fei
 * @date 2023/07/02
 */
public abstract class RedisUtils {

    /**
     * 作用于确保幂等性的令牌验证 Redis 脚本。
     * <p>
     * 注意 {@link DefaultRedisScript#getSha1()} 使用了 {@code synchronized} 。
     *
     * @see RedisScriptConst#IDEMPOTENCY_TOKEN_LUA_SCRIPT
     */
    private static final RedisScript<Long> IDEMPOTENCY_TOKEN_CHECK_REDIS_SCRIPT = RedisScript.of
            (IDEMPOTENCY_TOKEN_LUA_SCRIPT, Long.class);

    /**
     * 验证作用于幂等性的令牌。
     *
     * @param redisTemplate {@code RedisTemplate} 实例
     * @param key           Redis 的键
     * @param value         Redis 的值
     * @return 如果幂等性令牌验证通过，则返回 true；否则返回 false
     * @see RedisScriptConst#IDEMPOTENCY_TOKEN_LUA_SCRIPT
     */
    public static <K, V> boolean checkIdempotencyToken(RedisTemplate<K, V> redisTemplate, K key, V value) {
        Long executeResult = redisTemplate.execute(IDEMPOTENCY_TOKEN_CHECK_REDIS_SCRIPT,
                Collections.singletonList(key), value);
        return (executeResult == null || executeResult == 0L);
    }

}
