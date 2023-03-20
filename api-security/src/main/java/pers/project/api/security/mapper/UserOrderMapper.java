package pers.project.api.security.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import pers.project.api.security.model.entity.UserOrder;

/**
 * 针对表【user_order (用户接口订单) 】的数据库操作 Mapper
 *
 * @author Luo Fei
 * @date 2023/03/20
 */
@Mapper
public interface UserOrderMapper extends BaseMapper<UserOrder> {

}




