package pers.project.api.security.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pers.project.api.security.model.dto.AccountVerificationCodeCheckDTO;
import pers.project.api.security.model.dto.UserAccountAuthorityDTO;
import pers.project.api.security.model.dto.UserAccountStatusDTO;
import pers.project.api.security.model.dto.UserRegistryDTO;
import pers.project.api.security.model.po.UserAccountPO;

/**
 * 针对表【user_account (用户帐户) 】的数据库操作 Service
 *
 * @author Luo Fei
 * @date 2023/03/23
 */
public interface UserAccountService extends IService<UserAccountPO> {

    /**
     * 创建新帐户
     *
     * @param userRegistryDTO 用户注册 VO
     */
    void createNewAccount(UserRegistryDTO userRegistryDTO);

    /**
     * 生成 API 密钥
     *
     * @param accountCodeCheckDTO 账户验证码检查 DTO
     * @return {@code secretKey} 密钥值
     */
    String generateApiKey(AccountVerificationCodeCheckDTO accountCodeCheckDTO);

    /**
     * 获取 {@code secretKey}
     *
     * @param accountCodeCheckDTO 账户验证码检查 DTO
     * @return {@code secretKey} 密钥值
     */
    String getSecretKey(AccountVerificationCodeCheckDTO accountCodeCheckDTO);



    /**
     * 更新账户状态
     *
     * @param accountStatusDTO 账户状态 DTO
     */
    void updateAccountStatus(UserAccountStatusDTO accountStatusDTO);

    /**
     * 更新帐户权限
     *
     * @param accountAuthorityDTO 账户权限 DTO
     */
    void updateAccountAuthority(UserAccountAuthorityDTO accountAuthorityDTO);

}
