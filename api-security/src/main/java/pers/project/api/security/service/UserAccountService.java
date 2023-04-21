package pers.project.api.security.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pers.project.api.security.model.dto.UserAccountAuthorityDTO;
import pers.project.api.security.model.dto.UserAccountStatusDTO;
import pers.project.api.security.model.dto.UserRegistryDTO;
import pers.project.api.security.model.po.UserAccountPO;
import pers.project.api.security.model.vo.ApiKeyPairVO;

/**
 * 针对表【user_account (用户帐户) 】的数据库操作 Service
 *
 * @author Luo Fei
 * @date 2023/03/23
 */
public interface UserAccountService extends IService<UserAccountPO> {

    /**
     * 保存新帐户
     *
     * @param userRegistryDTO 用户注册 VO
     */
    void saveNewAccount(UserRegistryDTO userRegistryDTO);

    /**
     * 获取 API 密钥对 VO
     *
     * @param accountId 账户主键
     * @return API 密钥对 VO
     */
    ApiKeyPairVO getApiKeyPairVO(String accountId);

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
