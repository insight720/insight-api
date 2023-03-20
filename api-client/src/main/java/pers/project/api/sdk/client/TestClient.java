package pers.project.api.sdk.client;

import com.alibaba.fastjson2.JSON;
import lombok.Data;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import pers.project.api.sdk.model.Test;
import pers.project.api.sdk.util.SignUtils;

import java.net.URI;
import java.util.concurrent.CompletableFuture;
import java.util.random.RandomGenerator;
import java.util.random.RandomGeneratorFactory;

/**
 * 测试客户端
 *
 * @author Luo Fei
 * @date 2023/03/16
 */
@Data
public class TestClient {

    private static final WebClient WEB_CLIENT = WebClient.builder().build();

    public static final RandomGeneratorFactory<RandomGenerator>
            L32_X64_MIX_RANDOM = RandomGeneratorFactory.of("L32X64MixRandom");

    public static final String GATEWAY_HOST = "http://localhost:80/gateway";

    private String accessKey;

    private String secretKey;

    public TestClient(String accessKey, String secretKey) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

    public String post(Test test, String cookieName, String cookieValue) {
        String requestBody = JSON.toJSONString(test);
        CompletableFuture<String> bodyFuture = WEB_CLIENT
                .post()
                .uri(URI.create(GATEWAY_HOST + "/provider/test/post"))
                .cookie(cookieName, cookieValue)
                .bodyValue(requestBody)
                .headers(httpHeaders -> httpHeaders.addAll(getHeaders(requestBody)))
                .retrieve()
                .bodyToMono(String.class)
                .toFuture();
        return bodyFuture.join();
    }


    public HttpHeaders getHeaders(String requestBody) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        httpHeaders.add("accessKey", accessKey);
        httpHeaders.add("nonce", String.valueOf(L32_X64_MIX_RANDOM.create(System.currentTimeMillis()).nextLong(10000L)));
        httpHeaders.add("timestamp", String.valueOf(System.currentTimeMillis() / 1000));
        httpHeaders.add("sign", SignUtils.sign("test", secretKey));
        httpHeaders.add("body", "test");
        return httpHeaders;
    }

}
