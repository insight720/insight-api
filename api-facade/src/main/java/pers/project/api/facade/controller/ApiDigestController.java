package pers.project.api.facade.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.project.api.common.model.Result;
import pers.project.api.common.util.ResultUtils;
import pers.project.api.facade.model.query.ApiDigestPageQuery;
import pers.project.api.facade.model.vo.ApiDigestPageVO;
import pers.project.api.facade.service.ApiDigestService;

/**
 * 接口摘要控制器
 *
 * @author Luo Fei
 * @date 2023/06/01
 */
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/digest")
public class ApiDigestController {

    private final ApiDigestService apiDigestService;

    @PostMapping("/page")
    public Result<ApiDigestPageVO> viewApiDigestPage(@Valid @RequestBody ApiDigestPageQuery pageQuery) {
        ApiDigestPageVO pageVO = apiDigestService.getApiDigestPageVO(pageQuery);
        return ResultUtils.success(pageVO);
    }

}
