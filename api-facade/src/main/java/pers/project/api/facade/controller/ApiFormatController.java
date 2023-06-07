package pers.project.api.facade.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.project.api.common.model.Result;
import pers.project.api.common.util.ResultUtils;
import pers.project.api.common.validation.constraint.SnowflakeId;
import pers.project.api.facade.model.vo.ApiFormatVO;
import pers.project.api.facade.service.ApiFormatService;

/**
 * 接口格式控制器
 *
 * @author Luo Fei
 * @date 2023/06/01
 */
@Validated
@RestController
@RequestMapping("/format")
@RequiredArgsConstructor
public class ApiFormatController {

    private final ApiFormatService apiFormatService;

    @GetMapping("/{digestId}")
    public Result<ApiFormatVO> viewApiFormat(@SnowflakeId @PathVariable String digestId) {
        ApiFormatVO apiFormatVO = apiFormatService.getApiFormatVO(digestId);
        return ResultUtils.success(apiFormatVO);
    }

}
