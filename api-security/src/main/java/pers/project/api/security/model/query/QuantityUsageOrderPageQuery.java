package pers.project.api.security.model.query;

import lombok.Data;
import pers.project.api.common.validation.constraint.SnowflakeId;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * 接口计数用法订单分页 Query
 *
 * @author Luo Fei
 * @date 2023/07/08
 */
@Data
public class QuantityUsageOrderPageQuery {

    /**
     * 每页显示条数
     */
    private Long size;

    /**
     * 当前页
     */
    private Long current;

    /**
     * 账户主键
     */
    @SnowflakeId
    private String accountId;

    /**
     * 下单的调用次数
     */
    private String quantity;

    /**
     * 订单编号（模糊查询）
     */
    private String orderSn;

    /**
     * 订单描述（模糊查询）
     */
    private String description;

    /**
     * 订单状态集合
     */
    private Set<Integer> orderStatusSet;

    /**
     * 创建时间范围
     * <p>
     * [0] 起始时间
     * <p>
     * [1] 终止时间（包括）
     */
    private LocalDateTime[] createTimeRange;

    /**
     * 更新时间范围
     * <p>
     * [0] 起始时间
     * <p>
     * [1] 终止时间（包括）
     */
    private LocalDateTime[] updateTimeRange;

}
