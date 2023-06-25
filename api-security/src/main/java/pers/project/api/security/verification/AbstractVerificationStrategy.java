package pers.project.api.security.verification;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.RedisScript;

import java.time.Duration;
import java.util.Collections;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 抽象验证策略
 *
 * @author Luo Fei
 * @date 2023/04/17
 */
public abstract class AbstractVerificationStrategy implements VerificationStrategy {

    protected final RedisTemplate<String, Object> redisTemplate;

    /**
     * 用于生成验证代码的格式字符串。
     *
     * @see #generateVerificationCode()
     */
    private static final String VERIFICATION_CODE_FORMAT = "%06d";

    /**
     * 验证码值的上限（不包括）
     *
     * @see #generateVerificationCode()
     */
    private static final int VERIFICATION_CODE_UPPER_BOUND = 1_000_000;

    /**
     * 原子性 Redis 脚本，用于比较并删除键值对。
     *
     * <p>该脚本会使用给定的参数与指定键的值进行比较。如果值匹配，则原子地删除该键值对并返回 1；否则，不做任何操作并返回 0。</p>
     * <p>该脚本将以下两个参数传递给 Redis 服务器：
     * <ul>
     *
     * <li>KEYS[1] - 要比较和删除的键</li>
     *
     * <li>ARGV[1] - 用于匹配的值</li>
     *
     * </ul></p>
     *
     * @param <T> 返回值类型
     */
    private static final RedisScript<Long> DELETE_IF_MATCH_SCRIPT = RedisScript.of
            ("""
                    if ARGV[1] == redis.call('GET', KEYS[1]) then
                      return redis.call('del', KEYS[1])
                    else
                      return 0
                    end
                      """, Long.class);

    protected AbstractVerificationStrategy(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 生成验证码
     * <p>
     * 如果有必要，将添加前导零以确保长度为六位数。
     *
     * @return 一个 6 位的纯数字验证码。
     */
    protected String generateVerificationCode() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int numberCode = random.nextInt(VERIFICATION_CODE_UPPER_BOUND);
        return String.format(VERIFICATION_CODE_FORMAT, numberCode);
    }

    /**
     * 存储上下文信息和验证码到 Redis 中，并设置过期时间。
     *
     * @param keyPrefix   存储键前缀
     * @param contextInfo 上下文信息（如邮箱号、手机号等）
     * @param code        用户输入的验证码
     */
    protected void saveVerificationCodeInRedis(String keyPrefix, String contextInfo,
                                               String code, int validityPeriod) {
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        String codeKey = keyPrefix + contextInfo;
        valueOperations.set(codeKey, code, Duration.ofMinutes(validityPeriod));
    }

    /**
     * 检查用户输入的验证码是否与 Redis 存储的验证码相同。
     *
     * @param keyPrefix            存储键前缀
     * @param contextInfo          上下文信息（如邮箱号、手机号等）
     * @param userVerificationCode 用户输入的验证码
     * @return 如果检查通过，则返回 {@code true}，否则返回 {@code false}。
     */
    protected boolean checkRedisVerificationCode(String keyPrefix, String contextInfo,
                                                 String userVerificationCode) {

        String codeKey = keyPrefix + contextInfo;
        Long executeResult = redisTemplate.execute
                (DELETE_IF_MATCH_SCRIPT, Collections.singletonList(codeKey),
                        userVerificationCode);
        return (executeResult != null && executeResult == 1L);
    }

}
