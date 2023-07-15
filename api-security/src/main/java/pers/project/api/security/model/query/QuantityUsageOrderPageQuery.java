package pers.project.api.security.model.query;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import pers.project.api.common.validation.constraint.ContainedIn;
import pers.project.api.common.validation.constraint.NullOrNotBlank;
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
    @NotNull
    @Positive
    private Long size;

    /**
     * 当前页
     */
    @NotNull
    @Positive
    private Long current;

    /**
     * 账户主键
     */
    @SnowflakeId
    private String accountId;

    /**
     * 下单的调用次数
     */
    @NullOrNotBlank
    private String quantity;

    /**
     * 订单编号（模糊查询）
     */
    @NullOrNotBlank
    private String orderSn;

    /**
     * 订单描述（模糊查询）
     */
    @NullOrNotBlank
    private String description;

    /**
     * 订单状态集合
     */
    @ContainedIn(values = {"0", "1", "2", "3", "4", "5"}, element = Integer.class)
    private Set<Integer> orderStatusSet;

    /**
     * 创建时间范围
     * <p>
     * [0] 起始时间
     * <p>
     * [1] 终止时间（包括）
     */
    @Size(min = 2, max = 2)
    private LocalDateTime[] createTimeRange;

    /**
     * 更新时间范围
     * <p>
     * [0] 起始时间
     * <p>
     * [1] 终止时间（包括）
     */
    @Size(min = 2, max = 2)
    private LocalDateTime[] updateTimeRange;

}
