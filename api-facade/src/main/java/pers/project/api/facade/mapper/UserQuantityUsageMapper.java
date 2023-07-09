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
     * 根据 ID 更新已确认的调用次数存量。
     *
     * @param usageId  用户接口计数用法主键
     * @param quantity 要更新的数量
     */
    int updateConfirmedStockById(@Param("usageId") String usageId,
                                 @Param("quantity") String quantity);

    /**
     * 通过 usageId 物理删除。
     * <p>
     * 用于删除新建但因订单取消而不再使用的用户接口计数用法。
     *
     * @param usageId 用户接口计数用法主键
     */
    void deletePhysicallyByAccountId(@Param("usageId") String usageId);

}




