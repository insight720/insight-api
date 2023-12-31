package pers.project.api.facade.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import pers.project.api.facade.mapper.UserQuantityUsageMapper;
import pers.project.api.facade.model.po.UserQuantityUsagePO;
import pers.project.api.facade.service.UserQuantityUsageService;

/**
 * 针对表【user_quantity_usage (用户接口计数用法) 】的数据库操作 Service 实现
 *
 * @author Luo Fei
 * @date 2023/05/04
 */
@Service
public class UserQuantityUsageServiceImpl extends ServiceImpl<UserQuantityUsageMapper, UserQuantityUsagePO> implements UserQuantityUsageService {

}




