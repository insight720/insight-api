package pers.project.api.common.model.dto;

import lombok.Data;

/**
 * Insight API 计数用法接口的信息 DTO
 * <p>
 * 目前用于 Gateway 的请求次数的统计。
 * <p>
 * 如果查询状态信息还可以通过状态信息判断接口是否可用，这部分暂时先不写。
 *
 * @author Luo Fei
 * @date 2023/07/15
 */
@Data
public class QuantityUsageApiInfoDTO {

    /**
     * 接口摘要主键
     */
    private String digestId;

    /**
     * 用户接口计数用法主键
     */
    private String usageId;

}
