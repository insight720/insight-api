package pers.project.api.gateway.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.Assert;
import org.springframework.web.server.ServerWebExchange;
import pers.project.api.gateway.exeception.InsightApiGatewayException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;

import static pers.project.api.gateway.enumaration.SignatureRequestHeaderEnum.ORIGINAL_URL;
import static pers.project.api.gateway.enumaration.SignatureRequestHeaderEnum.USAGE_TYPE;


/**
 * Insight API 签名请求头工具类
 * <p>
 * 与  {@code pers.project.api.client.util.SignatureHeaderUtils} 保持相对一致。
 *
 * @author Luo Fei
 * @date 2023/07/13
 */
@Slf4j
public abstract class SignatureHeaderUtils {

    private static final String HMAC_SHA_256 = "HmacSHA256";

    private static final String HTTPS_SCHEMA = "https:";

    private static final Mac MAC;

    static {
        try {
            MAC = Mac.getInstance(HMAC_SHA_256);
        } catch (NoSuchAlgorithmException e) {
            throw new InsightApiGatewayException();
        }
    }

    /**
     * 获取时间戳
     *
     * @return 时间戳的字符串表示
     */
    @SuppressWarnings("all")
    public static long getTimestamp() {
        // 考虑性能问题可以使用 com.baomidou.mybatisplus.core.toolkit.SystemClock
        return System.currentTimeMillis();
    }

    /**
     * 获取签名
     *
     * @param secretKey 密钥
     * @param exchange  HTTP 请求-响应交互的协定
     * @return 签名字符串
     */
    public static String getSign(String secretKey, ServerWebExchange exchange) {
        // 拼接 method 和 url（带带路径变量和请求参数的 URL）
        ServerHttpRequest request = exchange.getRequest();
        StringBuilder inputStringBuilder = new StringBuilder();
        // https -> nginx -> http -> gateway  raw -> encoded (maybe) -> decoded
        // https + //insightapi.cn/gateway/provider/ip/searcher
        String urlWithVariablesAndParams = HTTPS_SCHEMA +
                // getSchemeSpecificPart()'s result is never null, and it's decoded
                exchange.getRequest().getURI().getSchemeSpecificPart();
        // 这部分仅在测试环境有用
        if (log.isDebugEnabled()) {
            // localhost:80 会被改写为 localhost
            log.debug("Original urlWithVariablesAndParams: {}", urlWithVariablesAndParams);
            urlWithVariablesAndParams = urlWithVariablesAndParams
                    .replaceFirst("https://localhost", "http://localhost:80");
            log.debug("Replaced urlWithVariablesAndParams: {}", urlWithVariablesAndParams);
        }
        inputStringBuilder.append(request.getMethod())
                .append(urlWithVariablesAndParams);
        // 拼接 requestHeader
        HttpHeaders headers = request.getHeaders();
        appendHeader(inputStringBuilder, headers,
                USAGE_TYPE.getHeaderName(), ORIGINAL_URL.getHeaderName());
        // 拼接 requestBody 的 sha256Hex 摘要
        String cachedRequestBody = GatewayHttpUtils.getCachedRequestBody(exchange);
        if (cachedRequestBody != null) {
            // 请求体有三种类型 InputStream，byte[] 和 String，但数据都被缓存为 String
            inputStringBuilder.append(DigestUtils.sha256Hex(cachedRequestBody));
        }
        // 获取 SIGN 值
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), HMAC_SHA_256);
        try {
            MAC.init(secretKeySpec);
        } catch (InvalidKeyException e) {
            throw new InsightApiGatewayException(e);
        }
        String inputString = inputStringBuilder.toString();
        if (log.isInfoEnabled()) {
            log.info("Calculate sever sign, inputString: {}", inputString);
        }
        byte[] hmacBytes = MAC.doFinal(inputString.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(hmacBytes);
    }

    private static void appendHeader(StringBuilder inputStringBuilder, HttpHeaders headers, String... headerNames) {
        for (String headerName : headerNames) {
            List<String> headerValueList = headers.get(headerName);
            Assert.isTrue(headerValueList != null && headerValueList.size() == 1,
                    "The headerValueList must be not null and have one element");
            inputStringBuilder.append(headerName)
                    .append(headerValueList.get(0));
        }
    }

}
