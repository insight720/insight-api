package pers.project.api.facade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import pers.project.api.facade.model.po.UserQuantityUsagePO;

/**
 * 针对表【user_quantity_usage (用户接口计数用法) 】的数据库操作 Mapper
 *
 * @author Luo Fei
 * @date 2023/05/04
 */
@Mapper
public interface UserQuantityUsageMapper extends BaseMapper<UserQuantityUsagePO> {

    /**
     * 根据账户 ID 和接口摘要 ID 更新调用次数存量。
     *
     * @param accountId 账户 ID
     * @param digestId  接口摘要 ID
     * @param quantity  要更新的数量
     */
    void updateStockByAccountIdAndDigestId(@Param("accountId") String accountId,
                                           @Param("digestId") String digestId,
                                           @Param("quantity") String quantity);

}




