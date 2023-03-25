package pers.project.api.security.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pers.project.api.security.model.entity.UserAccount;
import pers.project.api.security.model.vo.UserRegistryVO;

/**
 * 针对表【user_account (用户帐户) 】的数据库操作 Service
 *
 * @author Luo Fei
 * @date 2023/03/23
 */
public interface UserAccountService extends IService<UserAccount> {


    /**
     * 保存帐户
     *
     * @param userRegistryVO 用户注册 VO
     */
    void saveAccount(UserRegistryVO userRegistryVO);

}
