package pers.project.api.gateway.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pers.project.api.gateway.common.BaseResponse;
import pers.project.api.gateway.model.ApiInfo;

/**
 * Facade 远程服务
 *
 * @author Luo Fei
 * @date 2023/3/9
 */
@FeignClient(name = "facade")
public interface FacadeFeignService {

    @GetMapping("/facade/getApiInfo")
    BaseResponse<ApiInfo> getApiInfo(@RequestParam("url") String url,
                                     @RequestParam("method") String method);

}
