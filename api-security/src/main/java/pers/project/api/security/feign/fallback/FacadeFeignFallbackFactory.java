package pers.project.api.security.feign.fallback;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import pers.project.api.common.model.Result;
import pers.project.api.common.model.query.ApiAdminPageQuery;
import pers.project.api.common.model.query.UserApiDigestPageQuery;
import pers.project.api.common.model.query.UserApiFormatAndQuantityUsageQuery;
import pers.project.api.common.model.vo.ApiAdminPageVO;
import pers.project.api.common.model.vo.UserApiDigestPageVO;
import pers.project.api.common.model.vo.UserApiFormatAndQuantityUsageVO;
import pers.project.api.security.feign.FacadeFeignClient;

/**
 * Facade 项目 Feign 降级工厂
 *
 * @author Luo Fei
 * @date 2023/05/29
 */
@Slf4j
@Component
public class FacadeFeignFallbackFactory implements FallbackFactory<FacadeFeignClient> {

    @Override
    public FacadeFeignClient create(Throwable cause) {
        return new FacadeFeignClient() {

            @Override
            public Result<UserApiDigestPageVO> getUserApiDigestPageResult(UserApiDigestPageQuery pageQuery) {
                return new Result<>();
            }

            @Override
            public Result<UserApiFormatAndQuantityUsageVO> getUserApiFormatAndQuantityUsageResult(UserApiFormatAndQuantityUsageQuery query) {
                return new Result<>();
            }

            @Override
            public Result<ApiAdminPageVO> getApiAdminPageResult(ApiAdminPageQuery pageQuery) {
                return new Result<>();
            }

        };

    }

}
