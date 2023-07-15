package pers.project.api.gateway.feign;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import pers.project.api.common.model.Result;
import pers.project.api.common.model.dto.ClientUserInfoDTO;
import pers.project.api.common.model.query.ClientUserInfoQuery;


/**
 * Security 远程服务
 *
 * @author Luo Fei
 * @date 2023/03/05
 */
@FeignClient(name = "gateway", contextId = "security", path = "/gateway/security")
public interface SecurityFeignService {

    @PostMapping("/request/user/info/result")
    Result<ClientUserInfoDTO> getClientUserInfoResult(@Valid @RequestBody ClientUserInfoQuery userInfoQuery);

}
