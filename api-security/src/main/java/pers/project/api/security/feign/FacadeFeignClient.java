package pers.project.api.security.feign;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import pers.project.api.common.model.Result;
import pers.project.api.common.model.query.ApiAdminPageQuery;
import pers.project.api.common.model.query.UserApiDigestPageQuery;
import pers.project.api.common.model.query.UserApiFormatAndQuantityUsageQuery;
import pers.project.api.common.model.vo.ApiAdminPageVO;
import pers.project.api.common.model.vo.UserApiDigestPageVO;
import pers.project.api.common.model.vo.UserApiFormatAndQuantityUsageVO;
import pers.project.api.security.feign.fallback.FacadeFeignFallbackFactory;

/**
 * Facade 项目 Feign 客户端
 *
 * @author Luo Fei
 * @date 2023/05/05
 */
@Validated
@FeignClient(name = "gateway", contextId = "facade", path = "/gateway/facade",
        fallbackFactory = FacadeFeignFallbackFactory.class)
public interface FacadeFeignClient {

    @PostMapping("/user/api/digest/page/result")
    Result<UserApiDigestPageVO> getUserApiDigestPageResult(@Valid @RequestBody UserApiDigestPageQuery pageQuery);

    @PostMapping("/user/api/format/and/quantity/usage/result")
    Result<UserApiFormatAndQuantityUsageVO> getUserApiFormatAndQuantityUsageResult(@Valid @RequestBody UserApiFormatAndQuantityUsageQuery query);

    @PostMapping("/api/admin/page/result")
    Result<ApiAdminPageVO> getApiAdminPageResult(@Valid @RequestBody ApiAdminPageQuery pageQuery);

}
