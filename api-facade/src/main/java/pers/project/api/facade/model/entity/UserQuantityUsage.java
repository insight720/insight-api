package pers.project.api.facade.model.entity;

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
 * 表【user_quantity_usage (用户接口计数用法) 】的数据 Entity
 *
 * @author Luo Fei
 * @date 2023/03/20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "user_quantity_usage")
public class UserQuantityUsage {

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 账户主键
     */
    private Long accountId;

    /**
     * 接口摘要主键
     */
    private Long digestId;

    /**
     * 总调用次数
     */
    private Long total;

    /**
     * 失败调用次数
     */
    private Long failure;

    /**
     * 调用次数存量
     */
    private Long stock;

    /**
     * 用法状态
     */
    private Integer usageStatus;

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