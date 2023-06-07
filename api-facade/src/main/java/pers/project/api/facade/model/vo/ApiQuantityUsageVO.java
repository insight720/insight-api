package pers.project.api.facade.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 接口计数用法 VO
 *
 * @author Luo Fei
 * @date 2023/06/01
 */
@Data
public class ApiQuantityUsageVO {

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
     * 锁定的调用次数存量
     */
    private Long lockedStock;

    /**
     * 用法状态
     */
    private Integer usageStatus;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

}
