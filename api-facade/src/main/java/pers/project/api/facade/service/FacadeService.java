package pers.project.api.facade.service;

import pers.project.api.common.model.dto.QuantityUsageOrderStatusUpdateDTO;
import pers.project.api.common.model.dto.QuantityUsageStockDeductionDTO;
import pers.project.api.common.model.dto.QuantityUsageStockReleaseDTO;
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

    /**
     * 更新接口计数用法扣减的存量。
     * <p>
     * 这个方法主要在本地事务中执行两个操作：
     * <pre>
     * 1. 扣减接口计数用法存量。
     * 2. 查询用户的接口计数用法的主键，如果还不存在则新建一个。
     * </pre>
     * {@code orderStatusUpdateDTO} 用于回复 Security 项目订单状态消息。
     *
     * @param stockDeductionDTO    接口计数用法存量扣减 DTO
     * @param orderStatusUpdateDTO 接口计数用法订单状态更新 DTO
     */
    void updateQuantityUsageDeductedStock(QuantityUsageStockDeductionDTO stockDeductionDTO,
                                          QuantityUsageOrderStatusUpdateDTO orderStatusUpdateDTO);

    /**
     * 更新计数用法释放的存量。
     * <p>
     * 这个方法主要在本地事务中执行一个操作：
     * <pre>
     * 1. 释放订单的接口技术用法存量。
     * </pre>
     *
     * @param stockReleaseDTO 接口计数用法存量释放 DTO
     */
    void updateQuantityUsageReleasedStock(QuantityUsageStockReleaseDTO stockReleaseDTO);

}
