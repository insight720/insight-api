package pers.project.api.security.verification;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import pers.project.api.common.util.RedisUtils;

import java.time.Duration;
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
     * 回滚 Redis 中的上下文信息和验证码。
     *
     * @param keyPrefix   存储键前缀
     * @param contextInfo 上下文信息（如邮箱号、手机号等）
     */
    protected void rollbackVerificationCodeInRedis(String keyPrefix, String contextInfo) {
        String codeKey = keyPrefix + contextInfo;
        redisTemplate.delete(codeKey);
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
        return RedisUtils.checkIdempotencyToken(redisTemplate, codeKey, userVerificationCode);
    }

}
