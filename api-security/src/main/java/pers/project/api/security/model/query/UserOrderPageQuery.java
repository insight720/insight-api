package pers.project.api.security.model.query;

import lombok.Data;
import pers.project.api.common.validation.constraint.SnowflakeId;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * 用户订单分页 Query
 *
 * @author Luo Fei
 * @date 2023/05/16
 */
@Data
public class UserOrderPageQuery {

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
     * 订单编号（模糊查询）
     */
    private String orderSn;

    /**
     * 订单描述（模糊查询）
     */
    private String description;

    /**
     * 接口用法类型集合
     */
    private Set<Integer> usageType;

    /**
     * 订单状态集合
     */
    private Set<Integer> orderStatus;

    /**
     * 创建时间范围
     * <p>
     * [0] 起始时间
     * <p>
     * [1] 终止时间（包括）
     */
    private LocalDateTime[] createTime;

    /**
     * 更新时间范围
     * <p>
     * [0] 起始时间
     * <p>
     * [1] 终止时间（包括）
     */
    private LocalDateTime[] updateTime;

}
