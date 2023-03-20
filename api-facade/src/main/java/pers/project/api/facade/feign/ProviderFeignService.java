package pers.project.api.facade.feign;

import jakarta.servlet.http.Cookie;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * @author Luo Fei
 * @date 2023/03/17
 */
@FeignClient("gateway")
public interface ProviderFeignService {

    @GetMapping(path = "/gateway/provider/test/get")
    String get(@RequestParam("test") String test, @RequestHeader HttpHeaders httpHeaders,
               @CookieValue(name = "SESSION", required = false) Cookie cookie);

}
