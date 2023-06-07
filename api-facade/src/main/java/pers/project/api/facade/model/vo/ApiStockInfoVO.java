package pers.project.api.facade.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 接口库存信息 VO
 *
 * @author Luo Fei
 * @date 2023/06/03
 */
@Data
public class ApiStockInfoVO {

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
     * 更新时间
     */
    private LocalDateTime updateTime;

}
