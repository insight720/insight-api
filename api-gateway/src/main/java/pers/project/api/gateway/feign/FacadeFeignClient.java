package pers.project.api.gateway.feign;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
public interface FacadeFeignClient {

    @PostMapping("/quantity/usage/api/info/result")
    Result<QuantityUsageApiInfoDTO> getQuantityUsageApiInfoResult(@Valid @RequestBody QuantityUsageApiInfoQuery apiInfoQuery);

}
