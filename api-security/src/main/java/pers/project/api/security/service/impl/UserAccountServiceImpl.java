package pers.project.api.security.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.project.api.common.exception.BusinessException;
import pers.project.api.security.crypto.Argon2KeyGenerator;
import pers.project.api.security.crypto.SecureRandomFactory;
import pers.project.api.security.mapper.UserAccountMapper;
import pers.project.api.security.mapper.UserProfileMapper;
import pers.project.api.security.model.dto.AccountAuthorityDTO;
import pers.project.api.security.model.dto.AccountStatusDTO;
import pers.project.api.security.model.dto.KeyPairDTO;
import pers.project.api.security.model.entity.UserAccount;
import pers.project.api.security.model.entity.UserProfile;
import pers.project.api.security.model.vo.UserRegistryVO;
import pers.project.api.security.service.UserAccountService;

import java.security.SecureRandom;

import static pers.project.api.common.enumeration.ErrorEnum.DATABASE_ERROR;
import static pers.project.api.common.enumeration.ErrorEnum.REGISTRY_ERROR;
import static pers.project.api.security.constant.AuthorityConst.ROLE_USER;
import static pers.project.api.security.crypto.Argon2KeyGenerator.SUGGESTED_SOURCE_LENGTH;


/**
 * 针对表【user_account (用户帐户) 】的数据库操作 Service 实现
 *
 * @author Luo Fei
 * @date 2023/03/23
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserAccountServiceImpl extends ServiceImpl<UserAccountMapper, UserAccount> implements UserAccountService {

    private final PasswordEncoder passwordEncoder;

    private final UserProfileMapper userProfileMapper;

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void saveAccount(UserRegistryVO userRegistryVO) {
        // 确认密码一致
        String rowPassword = userRegistryVO.getPassword();
        String confirmedPassword = userRegistryVO.getConfirmedPassword();
        if (!confirmedPassword.equals(rowPassword)) {
            throw new BusinessException(REGISTRY_ERROR, "你输入的两个密码不一致");
        }
        // 检查账户名是否重复
        String username = userRegistryVO.getUsername();
        boolean exists = lambdaQuery().eq(UserAccount::getUsername, username).exists();
        if (exists) {
            throw new BusinessException(REGISTRY_ERROR, "账户名已存在");
        }
        // 保存用户账户
        String encodedPassword = passwordEncoder.encode(rowPassword);
        UserAccount userAccount = new UserAccount();
        userAccount.setUsername(username);
        userAccount.setPassword(encodedPassword);
        userAccount.setAuthority(ROLE_USER);
        try {
            save(userAccount);
        } catch (Exception e) {
            // 罕见的情况，比如用户名重复
            String message = "Saving account failed, username: " + username;
            log.error(message, e);
            throw new BusinessException(DATABASE_ERROR, "创建账户失败，请稍后再试！");
        }
        // 保存用户资料
        UserProfile userProfile = new UserProfile();
        userProfile.setAccountId(userAccount.getId());
        userProfileMapper.insert(userProfile);
    }

    @Override
    public KeyPairDTO getKeyPairDTO(String accountId) {
        // 生成密钥对
        SecureRandom secureRandom = SecureRandomFactory.defaultRandom();
        byte[] sourceBytes = new byte[SUGGESTED_SOURCE_LENGTH];
        secureRandom.nextBytes(sourceBytes);
        String accountKey = Argon2KeyGenerator.generate(sourceBytes, secureRandom);
        String accessKey = Argon2KeyGenerator.generate(sourceBytes, secureRandom);
        // 保存密钥对
        LambdaUpdateWrapper<UserAccount> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(UserAccount::getAccountKey, accountKey);
        updateWrapper.set(UserAccount::getAccessKey, accessKey);
        updateWrapper.eq(UserAccount::getId, accountId);
        try {
            update(updateWrapper);
        } catch (Exception e) {
            // 罕见的情况，比如密钥重复
            String message = """
                    Failed to update key pair，accountId: %s, accountKey: %s, accessKey: %s
                    """.formatted(accountId, accountKey, accessKey);
            log.error(message, e);
            throw new BusinessException(DATABASE_ERROR, "创建密钥失败，请稍后再试！");
        }
        // 返回密钥数据
        KeyPairDTO keyPairDTO = new KeyPairDTO();
        keyPairDTO.setAccountKey(accountKey);
        keyPairDTO.setSecretKey(accessKey);
        return keyPairDTO;
    }

    @Override
    public void updateAccountStatus(AccountStatusDTO accountStatusDTO) {
        LambdaUpdateWrapper<UserAccount> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(UserAccount::getAccountStatus, accountStatusDTO.getStatusCode());
        updateWrapper.eq(UserAccount::getId, accountStatusDTO.getAccountId());
        update(updateWrapper);
    }

    @Override
    public void updateAccountAuthority(AccountAuthorityDTO accountAuthorityDTO) {
        LambdaUpdateWrapper<UserAccount> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(UserAccount::getAuthority, accountAuthorityDTO.getAuthority());
        updateWrapper.eq(UserAccount::getId, accountAuthorityDTO.getAccountId());
        update(updateWrapper);
    }

}




