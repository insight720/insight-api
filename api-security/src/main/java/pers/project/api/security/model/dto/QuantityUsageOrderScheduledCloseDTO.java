package pers.project.api.security.model.dto;

import lombok.Data;

/**
 * 接口计数用法订单定时关闭 DTO
 * <p>
 * 可根据业务需求添加字段。
 *
 * @author Luo Fei
 * @date 2023/07/03
 */
@Data
public class QuantityUsageOrderScheduledCloseDTO {

    /**
     * 订单号
     */
    private String orderSn;

}
