package pers.project.api.security.model.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 表【quantity_usage_order (接口计数用法订单) 】的数据 Entity
 *
 * @author Luo Fei
 * @date 2023/07/03
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "quantity_usage_order")
public class QuantityUsageOrderPO {

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 订单编号
     */
    private String orderSn;

    /**
     * 订单描述
     */
    private String description;

    /**
     * 账户主键
     */
    private String accountId;

    /**
     * 接口摘要主键
     */
    private String digestId;

    /**
     * 用户接口用法主键
     */
    private String usageId;

    /**
     * 下单的调用次数
     */
    private String quantity;

    /**
     * 订单状态
     */
    private Integer orderStatus;

    /**
     * 是否删除（1 表示删除，0 表示未删除）
     */
    @TableLogic
    private Integer isDeleted;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

}