package pers.project.api.facade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import pers.project.api.common.model.dto.UserQuantityUsageCreationDTO;
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
     * 根据 {@code creationDTO} 中提供的信息，更新用户接口计数用法的库存数量。
     *
     * @param creationDTO 用户接口计数用法创建 DTO
     */
    void updateStockByCreationDTO(@Param("creationDTO") UserQuantityUsageCreationDTO creationDTO);

}




