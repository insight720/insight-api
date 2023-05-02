package pers.project.api.security.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pers.project.api.security.model.dto.*;
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
     * @param userAccountRegistryDTO 用户注册 DTO
     */
    void createNewAccount(UserAccountRegistryDTO userAccountRegistryDTO);

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
     * @param modificationDTO API 密钥状态修改 DTO（包含验证码检查 DTO）
     */
    void updateApiKeyStatus(ApiKeyStatusModificationDTO modificationDTO);

    /**
     * 更新非管理员权限
     *
     * @param authorityDTO 非管理权限修改 DTO
     */
    void updateNonAdminAuthority(NonAdminAuthorityModificationDTO authorityDTO);

    /**
     * 更新用户名
     *
     * @param modificationDTO 账户名修改 DTO（包括验证码检查 DTO)
     */
    void updateUsername(UsernameModificationDTO modificationDTO);

    /**
     * 更新用户名和密码
     *
     * @param settingDTO 用户名和密码设置 DTO
     */
    void updateUsernameAndPassword(UsernameAndPasswordSettingDTO settingDTO);

    /**
     * 更新密码
     *
     * @param modificationDTO 密码修改 DTO
     */
    void updatePassword(PasswordModificationDTO modificationDTO);

    /**
     * 删除账户
     *
     * @param accountCodeCheckDTO 账户验证码检查 DTO
     */
    void removeAccount(AccountVerificationCodeCheckDTO accountCodeCheckDTO);

    /**
     * 保存手机号或邮箱
     *
     * @param accountCodeCheckDTO 账户验证码检查 DTO
     */
    void savePhoneNumberOrEmailAddress(AccountVerificationCodeCheckDTO accountCodeCheckDTO);

    /**
     * 删除手机号或邮箱
     *
     * @param accountCodeCheckDTO
     */
    void removePhoneNumberOrEmailAddress(AccountVerificationCodeCheckDTO accountCodeCheckDTO);

}
