package pers.project.api.gateway.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pers.project.api.common.model.Result;
import pers.project.api.common.model.entity.ApiInfoEntity;


/**
 * Facade 远程服务
 *
 * @author Luo Fei
 * @date 2023/03/09
 */
@FeignClient(name = "gateway", contextId = "facade", path = "/gateway/facade")
public interface FacadeFeignService {

    @GetMapping("/getApiInfo")
    Result<ApiInfoEntity> getApiInfo(@RequestParam("url") String url,
                                     @RequestParam("method") String method);

}
