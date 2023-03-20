package pers.project.api.security.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import pers.project.api.security.mapper.UserOrderMapper;
import pers.project.api.security.model.entity.UserOrder;
import pers.project.api.security.service.UserOrderService;

/**
 * 针对表【user_order (用户接口订单) 】的数据库操作 Service 实现
 *
 * @author Luo Fei
 * @date 2023/03/20
 */
@Service
public class UserOrderServiceImpl extends ServiceImpl<UserOrderMapper, UserOrder> implements UserOrderService {

}




