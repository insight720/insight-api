package pers.project.api.facade.controller;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pers.project.api.common.model.Response;
import pers.project.api.common.util.ResponseUtils;
import pers.project.api.facade.service.UserApiInfoService;


/**
 * 帖子接口
 *
 * @author yupi
 */
@RestController
@RequestMapping("/userApiInfo")
@Slf4j
public class UserApiInfoController {

    @Resource
    private UserApiInfoService userApiInfoService;

    // endregion

    // /userApiInfo
    @GetMapping("/invokeCount")
    public Response<Boolean> invokeCount(@RequestParam("apiInfoId") long apiInfoId,
                                         @RequestParam("userId") long userId) {
        return ResponseUtils.success(userApiInfoService.invokeCount(apiInfoId, userId));
    }

}
