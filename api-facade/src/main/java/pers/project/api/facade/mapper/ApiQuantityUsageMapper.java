package pers.project.api.facade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import pers.project.api.facade.model.po.ApiQuantityUsagePO;

/**
 * 针对表【api_quantity_usage (接口计数用法) 】的数据库操作 Mapper
 *
 * @author Luo Fei
 * @date 2023/05/04
 */
@Mapper
public interface ApiQuantityUsageMapper extends BaseMapper<ApiQuantityUsagePO> {

    /**
     * 根据接口摘要 ID {@code digestId}，更新计数用法库存数量 {@code quantity}。
     *
     * @param digestId 接口摘要 ID
     * @param quantity 更新的数量
     * @return 受影响的数据行数（0 或 1）
     */
    int updateStockByDigestId(@Param("digestId") String digestId, @Param("quantity") String quantity);

}




