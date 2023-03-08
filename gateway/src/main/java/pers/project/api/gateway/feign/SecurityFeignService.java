package pers.project.api.gateway.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import pers.project.api.gateway.common.BaseResponse;
import pers.project.api.gateway.model.ApiInfo;
import pers.project.api.gateway.model.User;

/**
 * @author Luo Fei
 * @date 2023/3/5
 */
@FeignClient(name = "security")
public interface SecurityFeignService {

    @GetMapping("/security/user/getInvokeUser")
    BaseResponse<User> getInvokeUser(@RequestParam("accessKey") String accessKey);


    @GetMapping("/security/apiInfo/getApiInfo")
    BaseResponse<ApiInfo> getApiInfo(@RequestParam("url") String url,
                                     @RequestParam("method") String method);

    @GetMapping("/security/userApiInfo/invokeCount")
    BaseResponse<Boolean> invokeCount(@RequestParam("apiInfoId") long apiInfoId,
                                      @RequestParam("userId") long userId);

}
