package pers.project.api.provider.controller;

/**
 * @author Luo Fei
 * @date 2023/07/16
 */

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dreamlu.mica.ip2region.core.Ip2regionSearcher;
import net.dreamlu.mica.ip2region.core.IpInfo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pers.project.api.common.model.Result;
import pers.project.api.common.util.ResultUtils;
import pers.project.api.common.validation.constraint.NullOrNotBlank;

import java.util.Map;

/**
 * Provider 项目控制器
 *
 * @author Luo Fei
 * @date 2023/07/16
 */
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
public class ProviderController {

    private final Ip2regionSearcher ip2regionSearcher;

    @GetMapping("/ip/searcher")
    public Result<String> test(@NotBlank @RequestParam("ip") String ip) {
        IpInfo ipInfo = ip2regionSearcher.memorySearch(ip);
        return ResultUtils.success(JSON.toJSONString(ipInfo));
    }

    @PostMapping("/get/what/you/send/{pathVariable}")
    public Result<String> test(@PathVariable(required = false) @NullOrNotBlank String pathVariable,
                               @RequestParam(required = false) @NullOrNotBlank String requestParam,
                               @RequestBody(required = false) @NullOrNotBlank String requestBody,
                               @RequestHeader Map<String, String> requestHeaderMap) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Content", "Response from Provider");
        if (pathVariable != null) {
            jsonObject.put("PathVariable", pathVariable);
        }
        if (requestParam != null) {
            jsonObject.put("RequestParam", requestParam);
        }
        if (requestBody != null) {
            jsonObject.put("RequestBody", requestBody);
        }
        jsonObject.put("RequestHeader", requestHeaderMap);
        return ResultUtils.success(jsonObject.toString());
    }

}
