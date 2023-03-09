package pers.project.api.sdk.client;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import pers.project.api.sdk.model.Test;
import pers.project.api.sdk.utils.SignUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * 测试客户端
 *
 * @author Luo Fei
 * @date 2023/2/25
 */
@Data
@Slf4j
public class TestClient {

    private String accessKey;

    private String secretKey;

    public static final String GATEWAY_HOST = "http://localhost:80/gateway";


    public TestClient(String accessKey, String secretKey) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

    public String get(String test) {
        return HttpRequest.get(GATEWAY_HOST + "/facade/test/get?test=" + test)
                .header(getHeaders(null))
                .execute()
                .body();
    }

    @SneakyThrows
    public String post(Test test) {
        String json = getJson(test);
        HttpResponse httpResponse = HttpRequest.post(GATEWAY_HOST + "/facade/test/post")
                .header(getHeaders(json))
                .body(json)
                .execute();
        log.error("响应内容 ===================== \n {}", httpResponse);
        return httpResponse
                .body();
    }

    private static String getJson(Test test) {
        return JSONUtil.toJsonStr(test);
    }

    private Map<String, List<String>> getHeaders(String body) {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("accessKey", accessKey);
        // 不能直接发送
//        headers.put("secretKey", secretKey);
        headers.put("nonce", String.valueOf(ThreadLocalRandom.current().nextLong(10000L)));
        headers.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000));
        headers.put("sign", SignUtils.genSign(body, secretKey));
        headers.put("body", body);
        return headers.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        entry -> Collections.singletonList(entry.getValue())));
    }


}
