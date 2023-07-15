package pers.project.api.client.util;

import org.apache.commons.codec.digest.DigestUtils;
import pers.project.api.client.InsightApiRequest;
import pers.project.api.client.exeception.InsightApiClientException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.concurrent.ThreadLocalRandom;

import static pers.project.api.client.enumaration.SignatureRequestHeaderEnum.ORIGINAL_URL;
import static pers.project.api.client.enumaration.SignatureRequestHeaderEnum.USAGE_TYPE;

/**
 * Insight API 签名请求头工具类
 * <p>
 * 与 {@code pers.project.api.gateway.util.SignatureHeaderUtils} 保持相对一致。
 *
 * @author Luo Fei
 * @date 2023/07/13
 */
public abstract class SignatureHeaderUtils {

    private static final String HMAC_SHA_256 = "HmacSHA256";

    private static final Mac MAC;

    static {
        try {
            MAC = Mac.getInstance(HMAC_SHA_256);
        } catch (NoSuchAlgorithmException e) {
            throw new InsightApiClientException(e);
        }
    }

    /**
     * 获取时间戳
     *
     * @return 时间戳的字符串表示
     */
    @SuppressWarnings("all")
    public static String getTimestamp() {
        // 考虑性能问题可以使用 com.baomidou.mybatisplus.core.toolkit.SystemClock
        return String.valueOf(System.currentTimeMillis());
    }


    /**
     * 获取随机数
     *
     * @return 随机数的字符串表示
     */
    public static String getNonce() {
        // ThreadLocalRandom 是线程安全且效率较高的随机数生成器
        ThreadLocalRandom random = ThreadLocalRandom.current();
        // 2 的 32 次方种可能
        return String.valueOf(random.nextInt());
    }

    /**
     * 获取签名
     *
     * @param secretKey                 密钥
     * @param urlWithVariablesAndParams 带路径变量和请求参数的 URL
     * @param insightApiRequest         Insight API 请求对象
     * @return 签名字符串
     */
    public static String getSign(String secretKey, String urlWithVariablesAndParams, InsightApiRequest insightApiRequest) {
        // 拼接 method 和 url（带路径变量请求参数）
        StringBuilder inputStringBuilder = new StringBuilder();
        inputStringBuilder.append(insightApiRequest.method())
                .append(urlWithVariablesAndParams);
        // 拼接 requestHeader
        inputStringBuilder.append(USAGE_TYPE.getHeaderName())
                .append(insightApiRequest.usageType())
                .append(ORIGINAL_URL.getHeaderName())
                .append(insightApiRequest.url());
        // 拼接 requestBody 的 sha256Hex 摘要
        insightApiRequest.requestBody().ifPresent(body -> {
            String requestBodyDigest;
            if (InputStream.class.isAssignableFrom(body.getClass())) {
                try {
                    requestBodyDigest = DigestUtils.sha256Hex((InputStream) body);
                } catch (IOException e) {
                    throw new InsightApiClientException(e);
                }
            } else if (body instanceof byte[] bytes) {
                requestBodyDigest = DigestUtils.sha256Hex(bytes);
            } else {
                requestBodyDigest = DigestUtils.sha256Hex((String) body);
            }
            inputStringBuilder.append(requestBodyDigest);
        });
        // 获取 SIGN 值
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), HMAC_SHA_256);
        try {
            MAC.init(secretKeySpec);
        } catch (InvalidKeyException e) {
            throw new InsightApiClientException(e);
        }
        byte[] hmacBytes = MAC.doFinal(inputStringBuilder.toString().getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(hmacBytes);
    }

}
