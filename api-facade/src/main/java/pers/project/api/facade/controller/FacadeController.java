package pers.project.api.facade.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pers.project.api.common.model.Result;
import pers.project.api.common.model.query.ApiAdminPageQuery;
import pers.project.api.common.model.query.UserApiDigestPageQuery;
import pers.project.api.common.model.query.UserApiFormatAndQuantityUsageQuery;
import pers.project.api.common.model.vo.ApiAdminPageVO;
import pers.project.api.common.model.vo.UserApiDigestPageVO;
import pers.project.api.common.model.vo.UserApiFormatAndQuantityUsageVO;
import pers.project.api.common.util.ResultUtils;
import pers.project.api.facade.model.query.ApiDigestPageQuery;
import pers.project.api.facade.model.vo.ApiDigestPageVO;
import pers.project.api.facade.service.FacadeService;

/**
 * Facade 模块控制器
 *
 * @author Luo Fei
 * @date 2023/05/04
 */
@Validated
@RestController
@RequiredArgsConstructor
public class FacadeController {

    private final FacadeService facadeService;

    @PostMapping("/api/digest/page")
    public Result<ApiDigestPageVO> viewApiDigestPage(@Valid @RequestBody ApiDigestPageQuery pageQuery) {
        ApiDigestPageVO pageVO = facadeService.getApiDigestPageVO(pageQuery);
        return ResultUtils.success(pageVO);
    }

    // region OpenFeign
    @PostMapping("/user/api/digest/page/result")
    public Result<UserApiDigestPageVO> getUserApiDigestPageResult
    (@Valid @RequestBody UserApiDigestPageQuery pageQuery) {
        UserApiDigestPageVO pageDTO = facadeService.getUserApiDigestPageDTO(pageQuery);
        return ResultUtils.success(pageDTO);
    }

    @PostMapping("/user/api/format/and/quantity/usage/result")
    public Result<UserApiFormatAndQuantityUsageVO> getUserApiFormatAndQuantityUsageResult
            (@Valid @RequestBody UserApiFormatAndQuantityUsageQuery query) {
        UserApiFormatAndQuantityUsageVO formatAndQuantityUsageVO
                = facadeService.getUserApiFormatAndQuantityUsageVO(query);
        return ResultUtils.success(formatAndQuantityUsageVO);
    }

    @PostMapping("/api/admin/page/result")
    public Result<ApiAdminPageVO> getApiAdminPageResult
            (@Valid @RequestBody ApiAdminPageQuery pageQuery) {
        ApiAdminPageVO pageVO = facadeService.getApiAdminPageVO(pageQuery);
        return ResultUtils.success(pageVO);
    }
    // endregion

}
