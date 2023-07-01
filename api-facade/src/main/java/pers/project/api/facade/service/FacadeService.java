package pers.project.api.facade.service;

import pers.project.api.common.model.query.ApiAdminPageQuery;
import pers.project.api.common.model.query.UserApiDigestPageQuery;
import pers.project.api.common.model.query.UserApiFormatAndQuantityUsageQuery;
import pers.project.api.common.model.vo.ApiAdminPageVO;
import pers.project.api.common.model.vo.UserApiDigestPageVO;
import pers.project.api.common.model.vo.UserApiFormatAndQuantityUsageVO;

/**
 * Facade 项目 Service
 *
 * @author Luo Fei
 * @date 2023/05/05
 */
public interface FacadeService {

    /**
     * 基于提供的 {@code UserApiDigestPageQuery}，返回一个 {@code UserApiDigestPageVO} 对象。
     *
     * @param pageQuery 用户 API 摘要分页 Query
     * @return 用户 API 摘要页面 VO
     */
    UserApiDigestPageVO getUserApiDigestPageDTO(UserApiDigestPageQuery pageQuery);

    /**
     * 基于提供的 {@code query}，返回一个 {@code UserApiFormatAndQuantityUsageVO} 对象，
     * 该对象包含了对应 API 的格式和计数用法的使用情况信息。
     *
     * @param query 账户主键和 API 摘要主键 Query
     * @return 用户 API 格式和计数用法 VO
     */
    UserApiFormatAndQuantityUsageVO getUserApiFormatAndQuantityUsageVO(UserApiFormatAndQuantityUsageQuery query);

    /**
     * 基于提供的 {@code ApiAdminPageQuery}，返回一个 {@code ApiAdminPageVO} 对象，
     * 该对象包含了 API 管理页面所需展示的信息。
     *
     * @param pageQuery 包含分页信息和查询条件的 API 管理页面 Query
     * @return API 管理页面 VO
     */
    ApiAdminPageVO getApiAdminPageVO(ApiAdminPageQuery pageQuery);

}
