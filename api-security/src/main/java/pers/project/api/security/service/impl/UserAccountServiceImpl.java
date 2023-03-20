package pers.project.api.security.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import pers.project.api.security.mapper.UserAccountMapper;
import pers.project.api.security.model.entity.UserAccount;
import pers.project.api.security.service.UserAccountService;

/**
 * 针对表【user_account (用户帐户) 】的数据库操作 Service 实现
 *
 * @author Luo Fei
 * @date 2023-03-20
 */
@Service
public class UserAccountServiceImpl extends ServiceImpl<UserAccountMapper, UserAccount> implements UserAccountService {

}




