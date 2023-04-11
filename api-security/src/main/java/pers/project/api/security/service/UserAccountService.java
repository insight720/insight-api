package pers.project.api.security.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pers.project.api.security.model.dto.AccountAuthorityDTO;
import pers.project.api.security.model.dto.AccountStatusDTO;
import pers.project.api.security.model.dto.KeyPairDTO;
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

    /**
     * 获取密钥对 DTO
     *
     * @param accountId 账户主键
     * @return 密钥对 DTO
     */
    KeyPairDTO getKeyPairDTO(String accountId);

    /**
     * 更新账户状态
     *
     * @param accountStatusDTO 账户状态 DTO
     */
    void updateAccountStatus(AccountStatusDTO accountStatusDTO);

    /**
     * 更新帐户权限
     *
     * @param accountAuthorityDTO 账户权限 DTO
     */
    void updateAccountAuthority(AccountAuthorityDTO accountAuthorityDTO);

}
