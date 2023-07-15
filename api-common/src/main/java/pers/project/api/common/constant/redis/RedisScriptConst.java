package pers.project.api.common.constant.redis;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Redis 脚本常量
 * <p>
 * 脚本命名规则参考：
 * <pre>
 * IDEMPOTENCY_TOKEN_CHECK_SCRIPT
 * 脚本作用_SCRIPT
 * </pre>
 *
 * @author Luo Fei
 * @date 2023/06/28
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RedisScriptConst {

    /**
     * 作用于幂等性的令牌验证 LUA 脚本
     *
     * <p>
     * 该脚本会使用给定的参数与指定键的值进行比较。如果值匹配，
     * 则原子地删除该键值对并返回 1；否则，不做任何操作并返回 0。
     * </p>
     * <p>该脚本将以下两个参数传递给 Redis 服务器：
     * <ul>
     * 脚本既可以用 Redisson 执行，也可以用
     *
     * <li>KEYS[1] - 要比较和删除的键</li>
     *
     * <li>ARGV[1] - 用于匹配的值</li>
     *
     * </ul></p>
     */
    public static final String IDEMPOTENCY_TOKEN_LUA_SCRIPT = """
            if ARGV[1] == redis.call('GET', KEYS[1]) then
              return redis.call('del', KEYS[1])
            else
              return 0
            end
              """;

}
