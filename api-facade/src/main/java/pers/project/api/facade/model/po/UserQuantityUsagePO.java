package pers.project.api.facade.model.po;

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
 * 表【user_quantity_usage (用户接口计数用法) 】的数据 PO
 *
 * @author Luo Fei
 * @date 2023/05/04
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "user_quantity_usage")
public class UserQuantityUsagePO {

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 账户主键
     */
    private String accountId;

    /**
     * 接口摘要主键
     */
    private String digestId;

    /**
     * 总调用次数
     */
    private String total;

    /**
     * 失败调用次数
     */
    private String failure;

    /**
     * 调用次数存量
     */
    private String stock;

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