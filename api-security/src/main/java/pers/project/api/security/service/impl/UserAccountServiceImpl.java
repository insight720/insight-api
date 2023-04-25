package pers.project.api.security.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import pers.project.api.common.exception.BusinessException;
import pers.project.api.common.model.security.CustomUserDetails;
import pers.project.api.security.crypto.SecureRandomFactory;
import pers.project.api.security.enumeration.VerificationStrategyEnum;
import pers.project.api.security.mapper.UserAccountMapper;
import pers.project.api.security.mapper.UserProfileMapper;
import pers.project.api.security.model.dto.*;
import pers.project.api.security.model.po.UserAccountPO;
import pers.project.api.security.model.po.UserProfilePO;
import pers.project.api.security.service.CustomUserDetailsService;
import pers.project.api.security.service.SecurityService;
import pers.project.api.security.service.UserAccountService;

import java.security.SecureRandom;
import java.util.Base64;

import static pers.project.api.common.enumeration.ErrorEnum.*;
import static pers.project.api.security.constant.AuthorityConst.ROLE_USER;
import static pers.project.api.security.enumeration.VerificationStrategyEnum.PHONE;


/**
 * 针对表【user_account (用户帐户) 】的数据库操作 Service 实现
 *
 * @author Luo Fei
 * @date 2023/03/23
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserAccountServiceImpl extends ServiceImpl<UserAccountMapper, UserAccountPO> implements UserAccountService {

    private static final Base64.Encoder BASE64_ENCODER = Base64.getEncoder().withoutPadding();

    private static final int SECRET_ID_BYTE_LENGTH = 16;

    private static final int SECRET_KEY_BYTE_LENGTH = 16;

    private final PasswordEncoder passwordEncoder;

    private final UserProfileMapper userProfileMapper;

    private final CustomUserDetailsService userDetailsService;

    private final SecurityService securityService;

    private final TransactionTemplate transactionTemplate;

    @Override
    public void createNewAccount(UserRegistryDTO userRegistryDTO) {
        // 校验注册数据
        validateUserRegistryDTO(userRegistryDTO);
        // 保存用户账户
        String encodedPassword = passwordEncoder.encode(userRegistryDTO.getPassword());
        UserAccountPO userAccountPO = new UserAccountPO();
        userAccountPO.setUsername(userRegistryDTO.getUsername());
        userAccountPO.setPassword(encodedPassword);
        // phoneNumber 和 email 有一个为 null
        VerificationCodeCheckDTO codeCheckDTO = userRegistryDTO.getCodeCheckDTO();
        userAccountPO.setPhoneNumber(codeCheckDTO.getPhoneNumber());
        userAccountPO.setEmailAddress(codeCheckDTO.getEmailAddress());
        // 默认为用户权限
        userAccountPO.setAuthority(ROLE_USER);
        // 编程式事务，与 @Transactional 一样具有默认的 propagation 和 isolation
        transactionTemplate.executeWithoutResult(ignored -> {
            try {
                save(userAccountPO);
            } catch (Exception e) {
                // 罕见的情况，比如用户名重复
                // 抛出异常会在 TransactionTemplate.execute 中执行回滚
                String message = "Saving account failed, userAccount: " + userAccountPO;
                log.warn(message, e);
                throw new BusinessException(DATABASE_ERROR, "创建账户失败，请稍后再试");
            }
            // 创建用户资料
            UserProfilePO userProfilePO = new UserProfilePO();
            userProfilePO.setAccountId(userAccountPO.getId());
            userProfileMapper.insert(userProfilePO);
        });
    }

    @Override
    public String generateApiKey(AccountVerificationCodeCheckDTO accountCodeCheckDTO) {
        // 检查验证码是否合法
        VerificationCodeCheckDTO codeCheckDTO = accountCodeCheckDTO.getCodeCheckDTO();
        securityService.checkVerificationCode(codeCheckDTO, null);
        // 使用 SecureRandom 为密钥生成提供安全随机数
        SecureRandom secureRandom = SecureRandomFactory.defaultRandom();
        // 安全随机数作为密钥源字节数组
        byte[] secretIdBytes = new byte[SECRET_ID_BYTE_LENGTH];
        byte[] secretKeyBytes = new byte[SECRET_KEY_BYTE_LENGTH];
        secureRandom.nextBytes(secretKeyBytes);
        secureRandom.nextBytes(secretKeyBytes);
        // 使用 Argon2 哈希算法生成唯一密钥
        String secretId = BASE64_ENCODER.encodeToString(secretIdBytes);
        String secretKey = BASE64_ENCODER.encodeToString(secretKeyBytes);
        // 保存唯一密钥对
        LambdaUpdateWrapper<UserAccountPO> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(UserAccountPO::getSecretId, secretId);
        updateWrapper.set(UserAccountPO::getSecretKey, secretKey);
        updateWrapper.eq(UserAccountPO::getId, accountCodeCheckDTO.getAccountId());
        try {
            update(updateWrapper);
        } catch (Exception e) {
            // 罕见的情况，比如密钥重复
            String message = """
                    Failed to update API key，accountId: %s, secretId: %s
                    """.formatted(accountCodeCheckDTO.getAccountId(), secretId);
            log.error(message, e);
            throw new BusinessException(DATABASE_ERROR, "创建密钥失败，请稍后再试");
        }
        // 更新 Spring Security 上下文中的用户资料
        CustomUserDetails userDetails = userDetailsService.getLoginUserDetails();
        userDetails.setSecretId(secretId);
        userDetailsService.updateLoginUserDetails(userDetails);
        // 返回密钥值
        return secretKey;
    }

    @Override
    public String getSecretKey(AccountVerificationCodeCheckDTO accountCodeCheckDTO) {
        // 检查验证码是否合法
        VerificationCodeCheckDTO codeCheckDTO = accountCodeCheckDTO.getCodeCheckDTO();
        securityService.checkVerificationCode(codeCheckDTO, null);
        // 查询密钥值
        LambdaQueryWrapper<UserAccountPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(UserAccountPO::getSecretKey);
        queryWrapper.eq(UserAccountPO::getId, accountCodeCheckDTO.getAccountId());
        UserAccountPO userAccountPO = getOne(queryWrapper);
        if (userAccountPO == null) {
            log.error("UserAccount not found, accountId: " + accountCodeCheckDTO.getAccountId());
            throw new BusinessException(DATABASE_ERROR, "服务器错误，请联系管理员");
        }
        String secretKey = userAccountPO.getSecretKey();
        if (secretKey == null) {
            throw new BusinessException(USER_REQUEST_ERROR, "你还没有密钥");
        }
        return secretKey;
    }

    @Override
    public void updateAccountStatus(UserAccountStatusDTO accountStatusDTO) {
        LambdaUpdateWrapper<UserAccountPO> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(UserAccountPO::getAccountStatus, accountStatusDTO.getStatusCode());
        updateWrapper.eq(UserAccountPO::getId, accountStatusDTO.getAccountId());
        update(updateWrapper);
    }

    @Override
    public void updateAccountAuthority(UserAccountAuthorityDTO accountAuthorityDTO) {
        LambdaUpdateWrapper<UserAccountPO> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(UserAccountPO::getAuthority, accountAuthorityDTO.getAuthority());
        updateWrapper.eq(UserAccountPO::getId, accountAuthorityDTO.getAccountId());
        update(updateWrapper);
    }

    /**
     * 验证用户注册 DTO
     *
     * @param userRegistryDTO 用户注册 DTO
     * @throws BusinessException 校验失败
     */
    private void validateUserRegistryDTO(UserRegistryDTO userRegistryDTO) {
        // 确认密码一致
        boolean isConfirmed = userRegistryDTO.getPassword()
                .equals(userRegistryDTO.getConfirmedPassword());
        if (!isConfirmed) {
            throw new BusinessException(REGISTRY_ERROR, "你输入的两个密码不一致");
        }
        // 检查验证码
        VerificationCodeCheckDTO codeCheckDTO = userRegistryDTO.getCodeCheckDTO();
        VerificationStrategyEnum strategyEnum
                = securityService.checkVerificationCode(codeCheckDTO, null);
        // 检查账户名是否重复
        boolean usernameExists = lambdaQuery()
                .eq(UserAccountPO::getUsername, userRegistryDTO.getUsername())
                .exists();
        if (usernameExists) {
            throw new BusinessException(REGISTRY_ERROR, "账户名已存在");
        }
        // 检查邮箱或手机号是否已被使用
        boolean isUsingPhone = PHONE.equals(strategyEnum);
        boolean phoneOrEmailExists = isUsingPhone ?
                lambdaQuery().eq(UserAccountPO::getPhoneNumber, codeCheckDTO.getPhoneNumber()).exists()
                : lambdaQuery().eq(UserAccountPO::getEmailAddress, codeCheckDTO.getEmailAddress()).exists();
        if (phoneOrEmailExists) {
            String argument = isUsingPhone ? "手机号" : "邮箱";
            throw new BusinessException(REGISTRY_ERROR, "%s 已被使用".formatted(argument));
        }
    }

}




