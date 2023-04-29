package pers.project.api.security.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pers.project.api.security.model.dto.AccountVerificationCodeCheckDTO;
import pers.project.api.security.model.dto.ApiKeyStatusDTO;
import pers.project.api.security.model.dto.NonAdminAuthorityDTO;
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
     * 更新 API 密钥状态
     *
     * @param apiKeyStatusDTO API 密钥状态 DTO（包含验证码检查 DTO）
     */
    void updateApiKeyStatus(ApiKeyStatusDTO apiKeyStatusDTO);

    /**
     * 更新非管理员权限
     *
     * @param authorityDTO 非管理权限 DTO
     */
    void updateNonAdminAuthority(NonAdminAuthorityDTO authorityDTO);

}
