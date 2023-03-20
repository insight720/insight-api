package pers.project.api.facade.feign;

import feign.RequestLine;
import jakarta.servlet.http.Cookie;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;


/**
 * @author Luo Fei
 * @version 2023/3/17
 */
@FeignClient("gateway")
public interface ProviderFeignService {

    @GetMapping(path = "/gateway/provider/test/get")
    String get(@RequestParam("test") String test, @RequestHeader HttpHeaders httpHeaders,
               @CookieValue(name = "SESSION", required = false) Cookie cookie);

}
