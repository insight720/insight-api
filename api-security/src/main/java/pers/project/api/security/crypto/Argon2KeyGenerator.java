package pers.project.api.security.crypto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.crypto.generators.Argon2BytesGenerator;
import org.bouncycastle.crypto.params.Argon2Parameters;
import org.springframework.util.Assert;

import java.security.SecureRandom;
import java.util.Base64;

/**
 * Argon2 密钥生成器
 * <p>
 * 使用 {@code SecureRandom} 生成的随机字节数组作为盐来生成唯一密钥。
 *
 * @author Luo Fei
 * @date 2023/04/03
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Argon2KeyGenerator {

    /**
     * 密钥字节数组长度
     */
    private static final int HASH_LENGTH = 16;

    /**
     * 盐的字节数组长度
     */
    private static final int SALT_LENGTH = HASH_LENGTH;

    /**
     * 建议的源字节数组长度
     */
    public static final int SUGGESTED_SOURCE_LENGTH = HASH_LENGTH;

    // region Argon2Parameters
    private static final int MEMORY_AS_KB = 1 << 10; // 2 的 10 次方，1024 KB
    private static final int ITERATIONS = 3;
    private static final int PARALLELISM = 1;
    // endregion

    /**
     * Base 64 编码器
     * <p>
     * 不会在输出末尾添加任何填充字符，产生非标准的 Base64 字符串。
     */
    private static final Base64.Encoder BASE64_ENCODER = Base64.getEncoder().withoutPadding();

    /**
     * 生成 Argon2 唯一密钥
     * <p>
     * 即使源字节数据相同，生成的密钥也几乎不可能重复。
     * 使用随机的源字节数据可以进一步确保密钥的唯一性。
     *
     * @param sourceBytes  源字节数据
     * @param secureRandom 安全随机数生成器
     * @return Argon2 唯一密钥
     */
    public static String generate(byte[] sourceBytes, SecureRandom secureRandom) {
        Assert.notNull(sourceBytes, "The sourceBytes must not be null");
        Assert.notNull(secureRandom, "The secureRandom must not be null");
        byte[] argon2Bytes = getArgon2Bytes(sourceBytes, secureRandom);
        return BASE64_ENCODER.encodeToString(argon2Bytes);
    }

    /**
     * Argon2 哈希方法
     * <pre>
     * Argon2 哈希可以根据不同的硬件环境和安全要求进行配置，从而满足不同应用场景的需求。
     * 使用并设置了以下参数：
     * ARGON2_id：指定 Argon2 函数类型为 ID（最常用的类型），还有其他两种类型：i 和 d。
     * ARGON2_VERSION_13：指定 Argon2 算法版本号，该版本是目前最新的版本，具有更好的安全性和性能。
     * MEMORY_AS_KB：指定了使用的内存大小（以千字节为单位），它会影响到函数的安全性和性能。建议根据服务器的硬件配置和预期负载来选择适当的值。
     * ITERATIONS：指定了函数迭代次数，也称为时间成本，它控制了攻击者需要花费的计算资源来破解密码。较高的迭代次数可以提高安全性，但会降低性能。
     * PARALLELISM：指定了函数并行度，即同时使用的线程数。较高的并行度可以提高性能，但也会降低安全性。
     * Salt：是用于增加密码强度的随机盐值。
     * 这些参数的选择需要平衡安全性和性能之间的关系，需要根据具体应用场景进行调整。
     * </pre>
     *
     * @param sourceBytes  源字节数据
     * @param secureRandom 安全随机数生成器
     * @return Argon2 哈希字节数组
     */
    private static byte[] getArgon2Bytes(byte[] sourceBytes, SecureRandom secureRandom) {
        // 获取盐值
        byte[] salt = new byte[SALT_LENGTH];
        secureRandom.nextBytes(salt);
        // 设置参数
        Argon2Parameters parameters = new Argon2Parameters.Builder(Argon2Parameters.ARGON2_id)
                .withVersion(Argon2Parameters.ARGON2_VERSION_13)
                .withMemoryAsKB(MEMORY_AS_KB)
                .withIterations(ITERATIONS)
                .withParallelism(PARALLELISM)
                .withSalt(salt)
                .build();
        // 生成数据
        Argon2BytesGenerator generator = new Argon2BytesGenerator();
        generator.init(parameters);
        byte[] argon2Bytes = new byte[HASH_LENGTH];
        generator.generateBytes(sourceBytes, argon2Bytes);
        return argon2Bytes;
    }

}
