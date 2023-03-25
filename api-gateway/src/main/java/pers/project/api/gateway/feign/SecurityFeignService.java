package pers.project.api.gateway.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pers.project.api.common.model.Result;
import pers.project.api.common.model.entity.UserEntity;


/**
 * Security 远程服务
 *
 * @author Luo Fei
 * @date 2023/03/05
 */
@FeignClient(name = "gateway", contextId = "security", path = "/gateway/security")
public interface SecurityFeignService {

    @GetMapping("/user/getInvokeUser")
    Result<UserEntity> getInvokeUser(@RequestParam("accessKey") String accessKey);

    @GetMapping("/userApiInfo/invokeCount")
    Result<Boolean> invokeCount(@RequestParam("apiInfoId") long apiInfoId,
                                @RequestParam("userId") long userId);

}
