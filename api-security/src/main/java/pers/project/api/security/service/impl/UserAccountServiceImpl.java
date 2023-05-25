package pers.project.api.security.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.StringUtils;
import pers.project.api.common.exception.BusinessException;
import pers.project.api.common.model.security.CustomUserDetails;
import pers.project.api.security.crypto.SecureRandomFactory;
import pers.project.api.security.enumeration.AccountStatusEnum;
import pers.project.api.security.enumeration.AcountAuthorityEnum;
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
import java.util.List;
import java.util.Set;

import static pers.project.api.common.enumeration.ErrorEnum.*;
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

    private final SessionRegistry sessionRegistry;

    private final TransactionTemplate transactionTemplate;

    @Override
    public void createNewAccount(UserAccountRegistryDTO userAccountRegistryDTO) {
        // 校验注册数据
        VerificationStrategyEnum strategyEnum = validateUserRegistryDTO(userAccountRegistryDTO);
        // 保存用户账户
        String encodedPassword = passwordEncoder.encode(userAccountRegistryDTO.getPassword());
        UserAccountPO userAccountPO = new UserAccountPO();
        userAccountPO.setUsername(userAccountRegistryDTO.getUsername());
        userAccountPO.setPassword(encodedPassword);
        // phoneNumber 和 email 有一个为 null
        VerificationCodeCheckDTO codeCheckDTO = userAccountRegistryDTO.getCodeCheckDTO();
        switch (strategyEnum) {
            case PHONE -> userAccountPO.setPhoneNumber(codeCheckDTO.getPhoneNumber());
            case EMAIL -> userAccountPO.setEmailAddress(codeCheckDTO.getEmailAddress());
        }
        // 默认为用户权限
        userAccountPO.setAuthority(AcountAuthorityEnum.ROLE_USER.name());
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
        secureRandom.nextBytes(secretIdBytes);
        secureRandom.nextBytes(secretKeyBytes);
        // 使用 Base64 编码为可读的字符串
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
    public void updateApiKeyStatus(ApiKeyStatusModificationDTO modificationDTO) {
        // 检查验证码是否合法
        VerificationCodeCheckDTO codeCheckDTO = modificationDTO.getCodeCheckDTO();
        securityService.checkVerificationCode(codeCheckDTO, null);
        // 修改 API 密钥状态
        AccountStatusEnum targetStatus
                = AccountStatusEnum.valueOf(modificationDTO.getNewStatus());
        LambdaUpdateWrapper<UserAccountPO> updateWrapper = new LambdaUpdateWrapper<>();
        Integer targetStatusCode = targetStatus.statusCode();
        updateWrapper.set(UserAccountPO::getAccountStatus, targetStatusCode);
        updateWrapper.eq(UserAccountPO::getId, modificationDTO.getAccountId());
        update(updateWrapper);
        // 更新 Session 中的账户状态
        CustomUserDetails userDetails = userDetailsService.getLoginUserDetails();
        userDetails.setAccountStatus(targetStatusCode);
        userDetailsService.updateLoginUserDetails(userDetails);
    }

    @Override
    public void updateNonAdminAuthority(NonAdminAuthorityModificationDTO authorityDTO) {
        // 不同权限以 , 分隔后存储于 authority 列中
        Set<String> targetAuthoritySet = authorityDTO.getNewAuthoritySet();
        String newAuthority = StringUtils.collectionToCommaDelimitedString(targetAuthoritySet);
        LambdaUpdateWrapper<UserAccountPO> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(UserAccountPO::getAuthority, newAuthority);
        updateWrapper.eq(UserAccountPO::getId, authorityDTO.getAccountId());
        update(updateWrapper);
        // 更新 Session 中的账户权限
        CustomUserDetails userDetails = userDetailsService.getLoginUserDetails();
        userDetails.setAuthoritySet(targetAuthoritySet);
        userDetailsService.updateLoginUserDetails(userDetails);
    }

    @Override
    public void updateUsername(UsernameModificationDTO modificationDTO) {
        // 检查验证码是否合法
        VerificationCodeCheckDTO codeCheckDTO = modificationDTO.getCodeCheckDTO();
        securityService.checkVerificationCode(codeCheckDTO, null);
        // 检查用户名是否重复
        String newUsername = modificationDTO.getNewUsername();
        throwExceptionIfUsernameDuplicate(newUsername);
        // 修改用户名
        LambdaUpdateWrapper<UserAccountPO> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(UserAccountPO::getUsername, newUsername);
        updateWrapper.eq(UserAccountPO::getId, modificationDTO.getAccountId());
        try {
            update(updateWrapper);
        } catch (Exception e) {
            // 罕见的情况，比如用户名重复
            log.warn("""
                    Update username failed, accountId: %s, newUsername: %s
                    """.formatted(modificationDTO.getAccountId(), newUsername), e);
            throw new BusinessException(DATABASE_ERROR, "修改用户名失败，请稍后再试");
        }
        // 更新 Session 中的用户名
        CustomUserDetails userDetails = userDetailsService.getLoginUserDetails();
        userDetails.setUsername(newUsername);
        userDetailsService.updateLoginUserDetails(userDetails);
    }

    @Override
    public void updateUsernameAndPassword(UsernameAndPasswordSettingDTO settingDTO) {
        // 检查验证码是否合法
        VerificationCodeCheckDTO codeCheckDTO = settingDTO.getCodeCheckDTO();
        securityService.checkVerificationCode(codeCheckDTO, null);
        // 检查用户名是否重复
        String newUsername = settingDTO.getNewUsername();
        throwExceptionIfUsernameDuplicate(newUsername);
        // 修改用户名和密码
        String encodedNewPassword = passwordEncoder.encode(settingDTO.getNewPassword());
        LambdaUpdateWrapper<UserAccountPO> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(UserAccountPO::getUsername, newUsername);
        updateWrapper.set(UserAccountPO::getPassword, encodedNewPassword);
        updateWrapper.eq(UserAccountPO::getId, settingDTO.getAccountId());
        try {
            update(updateWrapper);
        } catch (Exception e) {
            // 罕见的情况，比如用户名重复
            log.warn("""
                    Update username and password failed, accountId: %s, newUsername: %s
                    """.formatted(settingDTO.getAccountId(), newUsername), e);
            throw new BusinessException(DATABASE_ERROR, "设置用户名和密码失败，请稍后再试");
        }
        // 更新 Session 中的用户名和密码
        CustomUserDetails userDetails = userDetailsService.getLoginUserDetails();
        userDetails.setUsername(newUsername);
        userDetails.setPassword(encodedNewPassword);
        userDetailsService.updateLoginUserDetails(userDetails);
    }

    @Override
    public void updatePassword(PasswordModificationDTO modificationDTO) {
        VerificationCodeCheckDTO codeCheckDTO = modificationDTO.getCodeCheckDTO();
        if (codeCheckDTO != null) {
            // 检查验证码是否合法
            securityService.checkVerificationCode(codeCheckDTO, null);
        } else {
            // 检查原密码是否正确
            CustomUserDetails userDetails = userDetailsService.getLoginUserDetails();
            if (!passwordEncoder.matches
                    (modificationDTO.getOriginalPassword(), userDetails.getPassword())) {
                throw new BusinessException(PASSWORD_ERROR, "密码错误");
            }
        }
        // 修改密码
        String encodedNewPassword = passwordEncoder.encode(modificationDTO.getNewPassword());
        LambdaUpdateWrapper<UserAccountPO> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(UserAccountPO::getPassword, encodedNewPassword);
        updateWrapper.eq(UserAccountPO::getId, modificationDTO.getAccountId());
        update(updateWrapper);
        // 更新 Session 中的密码
        CustomUserDetails userDetails = userDetailsService.getLoginUserDetails();
        userDetails.setPassword(encodedNewPassword);
        userDetailsService.updateLoginUserDetails(userDetails);
    }

    @Override
    public void removeAccount(AccountVerificationCodeCheckDTO accountCodeCheckDTO) {
        VerificationCodeCheckDTO codeCheckDTO = accountCodeCheckDTO.getCodeCheckDTO();
        String accountId = accountCodeCheckDTO.getAccountId();
        if (codeCheckDTO != null) {
            // 检查验证码是否合法
            securityService.checkVerificationCode(codeCheckDTO, null);
        }
        // 逻辑删除账户
        removeById(accountId);
        // 下线当前用户
        CustomUserDetails userDetails = userDetailsService.getLoginUserDetails();
        List<SessionInformation> sessions = sessionRegistry.getAllSessions(userDetails, false);
        for (SessionInformation session : sessions) {
            session.expireNow();
        }
    }

    @Override
    public void savePhoneNumberOrEmailAddress(AccountVerificationCodeCheckDTO accountCodeCheckDTO) {
        // 检查验证码是否合法
        VerificationCodeCheckDTO codeCheckDTO = accountCodeCheckDTO.getCodeCheckDTO();
        VerificationStrategyEnum strategyEnum = securityService.checkVerificationCode(codeCheckDTO, null);
        // 根据绑定的内容修改数据
        CustomUserDetails userDetails = userDetailsService.getLoginUserDetails();
        SFunction<UserAccountPO, String> getter;
        String fieldValue;
        switch (strategyEnum) {
            case PHONE -> {
                getter = UserAccountPO::getPhoneNumber;
                fieldValue = codeCheckDTO.getPhoneNumber();
                userDetails.setPhoneNumber(fieldValue);
            }
            case EMAIL -> {
                getter = UserAccountPO::getEmailAddress;
                fieldValue = codeCheckDTO.getEmailAddress();
                userDetails.setEmailAddress(fieldValue);
            }
            default -> throw new IllegalStateException("Should never get here");
        }
        LambdaUpdateWrapper<UserAccountPO> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(getter, fieldValue);
        updateWrapper.eq(UserAccountPO::getId, accountCodeCheckDTO.getAccountId());
        update(updateWrapper);
        // 更新 Session 中的手机号或邮箱地址
        userDetailsService.updateLoginUserDetails(userDetails);
    }

    @Override
    public void removePhoneNumberOrEmailAddress(AccountVerificationCodeCheckDTO accountCodeCheckDTO) {
        // 检查验证码是否合法
        VerificationCodeCheckDTO codeCheckDTO = accountCodeCheckDTO.getCodeCheckDTO();
        VerificationStrategyEnum strategyEnum = securityService.checkVerificationCode(codeCheckDTO, null);
        // 根据绑定的内容修改数据
        CustomUserDetails userDetails = userDetailsService.getLoginUserDetails();
        SFunction<UserAccountPO, String> getter;
        switch (strategyEnum) {
            case PHONE -> {
                getter = UserAccountPO::getPhoneNumber;
                userDetails.setPhoneNumber(null);
            }
            case EMAIL -> {
                getter = UserAccountPO::getEmailAddress;
                userDetails.setEmailAddress(null);
            }
            default -> throw new IllegalStateException("Should never get here");
        }
        LambdaUpdateWrapper<UserAccountPO> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(getter, null);
        updateWrapper.eq(UserAccountPO::getId, accountCodeCheckDTO.getAccountId());
        update(updateWrapper);
        // 更新 Session 中的手机号或邮箱地址
        userDetailsService.updateLoginUserDetails(userDetails);
    }

    /**
     * 验证用户注册 DTO
     *
     * @param userAccountRegistryDTO 用户注册 DTO
     * @return 验证策略枚举（帮助基于策略的程序）
     * @throws BusinessException 业务异常（带有提示信息）
     */
    private VerificationStrategyEnum validateUserRegistryDTO(UserAccountRegistryDTO userAccountRegistryDTO) {
        // 检查验证码
        VerificationCodeCheckDTO codeCheckDTO = userAccountRegistryDTO.getCodeCheckDTO();
        VerificationStrategyEnum strategyEnum
                = securityService.checkVerificationCode(codeCheckDTO, null);
        // 检查账户名是否重复
        throwExceptionIfUsernameDuplicate(userAccountRegistryDTO.getUsername());
        // 检查邮箱或手机号是否已被使用
        boolean isUsingPhone = PHONE.equals(strategyEnum);
        boolean phoneOrEmailExists = isUsingPhone ?
                lambdaQuery().eq(UserAccountPO::getPhoneNumber, codeCheckDTO.getPhoneNumber()).exists()
                : lambdaQuery().eq(UserAccountPO::getEmailAddress, codeCheckDTO.getEmailAddress()).exists();
        if (phoneOrEmailExists) {
            String argument = isUsingPhone ? "手机号" : "邮箱";
            throw new BusinessException(REGISTRY_ERROR, "%s 已被使用".formatted(argument));
        }
        return strategyEnum;
    }

    /**
     * 如果用户名已经存在，则抛出 {@code BusinessException}。
     *
     * @param username 要检查是否已经存在中的用户名
     * @throws BusinessException 如果用户名已经存在
     */
    private void throwExceptionIfUsernameDuplicate(String username) {
        boolean usernameExists = lambdaQuery()
                .eq(UserAccountPO::getUsername, username)
                .exists();
        if (usernameExists) {
            throw new BusinessException(REGISTRY_ERROR, "账户名已存在");
        }
    }

}




