package pers.project.api.security.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import pers.project.api.security.model.po.QuantityUsageOrderPO;

/**
 * 针对表【quantity_usage_order (接口计数用法订单) 】的数据库操作 Mapper
 *
 * @author Luo Fei
 * @date 2023/07/03
 */
@Mapper
public interface QuantityUsageOrderMapper extends BaseMapper<QuantityUsageOrderPO> {

}




