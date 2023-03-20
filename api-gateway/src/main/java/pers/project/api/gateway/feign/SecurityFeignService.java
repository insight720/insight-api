package pers.project.api.gateway.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pers.project.api.common.model.Response;
import pers.project.api.common.model.entity.UserEntity;


/**
 * Security 远程服务
 *
 * @author Luo Fei
 * @version 2023/3/5
 */
@FeignClient(name = "gateway", contextId = "security", path = "/gateway/security")
public interface SecurityFeignService {

    @GetMapping("/user/getInvokeUser")
    Response<UserEntity> getInvokeUser(@RequestParam("accessKey") String accessKey);

    @GetMapping("/userApiInfo/invokeCount")
    Response<Boolean> invokeCount(@RequestParam("apiInfoId") long apiInfoId,
                                  @RequestParam("userId") long userId);

}
