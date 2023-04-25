package pers.project.api.security.crypto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.*;

/**
 * {@code SecureRandom} 工厂
 * <p>
 * {@code SecureRandom} 是线程安全的，可以单例使用。
 * 但其同步策略使用 {@code synchronized} 来添加类锁，
 * 可能造成阻塞。如果每次使用时创建新的实例，则会有额外的初始化开销。
 * <p>
 * 使用 {@code new SecureRandom()} 创建的实例，默认使用
 * {@code HashDrbg}，基于 SHA-256 算法，种子
 * 在 {@code AbstractDrbg#instantiateIfNecessary(byte[])} 方法中指定一次，
 * 种子数据取自 {@code AbstractDrbg.SeederHolder#seeder}，并且不会自动更新种子。
 *
 * @author Luo Fei
 * @date 2023/04/25
 */
public final class SecureRandomFactory {

    private static final Logger logger = LoggerFactory.getLogger(SecureRandomFactory.class);

    private SecureRandomFactory() {
    }

    /**
     * RNG (Random Number Generator) 算法
     * <p>
     * 默认值为 DRBG (Deterministic Random Bit Generator)，
     *
     * @see SecureRandom#getInstance(String)
     */
    private static final String DEFAULT_ALGORITHM = "DRBG";

    /**
     * RNG 算法 API 的提供者
     * <p>
     * {@link SecureRandomFactory#DEFAULT_ALGORITHM} 的默认值 DRBG 对应的提供者为 SUN。
     *
     * @see Provider
     */
    private static final String DEFAULT_PROVIDER_NAME = "SUN";

    /**
     * DRBG 默认参数
     * <p>
     * 实际上，这与 {@code new SecureRandom()} 所使用的参数相同。
     */
    private static final DrbgParameters.Instantiation DEFAULT_PARAMS = DrbgParameters.instantiation
            (128, DrbgParameters.Capability.RESEED_ONLY, null);

    /**
     * 创建默认的 {@code SecureRandom}
     * <p>
     * 此方法的存在是为了显式设置参数并支持调试，使用它与使用 {@code new SecureRandom()} 几乎完全相同，
     *
     * @return 默认的 {@code SecureRandom}
     * @see SecureRandom#SecureRandom()
     */
    public static SecureRandom defaultRandom() {
        return create(DEFAULT_ALGORITHM, DEFAULT_PROVIDER_NAME, DEFAULT_PARAMS);
    }

    /**
     * 创建特定的 {@code SecureRandom}
     * <p>
     * 此方法的存在是为了通过日志进行调试。
     *
     * @param algorithm    RNG 算法名称
     * @param providerName API 提供者名称
     * @param params       新创建的 {@code SecureRandom} 必须支持的参数
     * @return 特定的 {@code SecureRandom}
     * @see SecureRandom#getInstance(String, SecureRandomParameters, Provider)
     */
    public static SecureRandom create(String algorithm, String providerName,
                                      SecureRandomParameters params) {
        SecureRandom random;
        try {
            Provider provider = Security.getProvider(providerName);
            if (provider != null) {
                random = SecureRandom.getInstance(algorithm, params, provider);
                if (logger.isDebugEnabled()) {
                    String message = """
                            Security algorithm %s was found from provider %s. [For SecureRandom] %s
                            """.formatted(random.getAlgorithm(), random.getProvider(), random);
                    logger.debug(message);
                }
            } else {
                random = SecureRandom.getInstance(algorithm);
                String message = """
                        Security provider %s was not found, but algorithm %s \
                        was found from another provider %s. [For SecureRandom] %s
                        """.formatted(providerName, algorithm, random.getProvider(), random);
                logger.warn(message);
            }
        } catch (NoSuchAlgorithmException e) {
            random = new SecureRandom();
            String message = """
                    Security algorithm %s could not be found from %s or any other providers, \
                    and default algorithm is %s that provided by %s. [For SecureRandom] %s
                    """.formatted(algorithm, providerName, random.getAlgorithm(), random.getProvider(), random);
            logger.error(message);
        }
        return random;
    }

}
