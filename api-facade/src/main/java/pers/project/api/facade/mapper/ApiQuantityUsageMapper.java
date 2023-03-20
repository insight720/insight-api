package pers.project.api.facade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import pers.project.api.facade.model.entity.ApiQuantityUsage;

/**
 * 针对表【api_quantity_usage (接口计数用法) 】的数据库操作 Mapper
 *
 * @author Luo Fei
 * @date 2023/03/20
 */
@Mapper
public interface ApiQuantityUsageMapper extends BaseMapper<ApiQuantityUsage> {

}




