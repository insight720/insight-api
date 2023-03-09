package pers.project.api.gateway.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pers.project.api.gateway.common.BaseResponse;
import pers.project.api.gateway.model.User;

/**
 * Security 远程服务
 *
 * @author Luo Fei
 * @date 2023/3/5
 */
@FeignClient(name = "security")
public interface SecurityFeignService {

    @GetMapping("/security/user/getInvokeUser")
    BaseResponse<User> getInvokeUser(@RequestParam("accessKey") String accessKey);


    @GetMapping("/security/userApiInfo/invokeCount")
    BaseResponse<Boolean> invokeCount(@RequestParam("apiInfoId") long apiInfoId,
                                      @RequestParam("userId") long userId);

}
