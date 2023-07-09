package pers.project.api.facade.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pers.project.api.common.model.dto.QuantityUsageStockConfirmationDTO;
import pers.project.api.facade.model.po.UserQuantityUsagePO;

/**
 * 针对表【user_quantity_usage (用户接口计数用法) 】的数据库操作 Service
 *
 * @author Luo Fei
 * @date 2023/05/04
 */
public interface UserQuantityUsageService extends IService<UserQuantityUsagePO> {

    /**
     * 更新已确认的用户接口计数用法存量。
     *
     * @param stockConfirmationDTO 接口计数用法存量确认 DTO
     */
    void updateConfirmedStock(QuantityUsageStockConfirmationDTO stockConfirmationDTO);

}
