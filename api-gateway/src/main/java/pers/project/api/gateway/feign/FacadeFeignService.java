package pers.project.api.gateway.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import pers.project.api.common.model.Result;
import pers.project.api.common.model.dto.QuantityUsageApiInfoDTO;
import pers.project.api.common.model.query.QuantityUsageApiInfoQuery;


/**
 * Facade 远程服务
 *
 * @author Luo Fei
 * @date 2023/03/09
 */
@FeignClient(name = "gateway", contextId = "facade", path = "/gateway/facade")
public interface FacadeFeignService {

    @GetMapping("/quantity/usage/api/info/result")
    Result<QuantityUsageApiInfoDTO> getQuantityUsageApiInfoResult(QuantityUsageApiInfoQuery apiInfoQuery);

}
