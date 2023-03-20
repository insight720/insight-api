package pers.project.api.security.model.entity;

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
 * 表【user_order (用户接口订单) 】的数据 Entity
 *
 * @author Luo Fei
 * @date 2023/03/20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "user_order")
public class UserOrder {

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 订单编号
     */
    private String orderSn;

    /**
     * 账户主键
     */
    private Long accountId;

    /**
     * 接口摘要主键
     */
    private Long digestId;

    /**
     * 用户接口用法主键
     */
    private Long usageId;

    /**
     * 接口用法类型
     */
    private Integer usageType;

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