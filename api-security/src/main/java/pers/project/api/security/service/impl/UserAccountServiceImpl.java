package pers.project.api.security.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.project.api.common.exception.BusinessException;
import pers.project.api.common.exception.ServerException;
import pers.project.api.security.mapper.UserAccountMapper;
import pers.project.api.security.mapper.UserProfileMapper;
import pers.project.api.security.model.entity.UserAccount;
import pers.project.api.security.model.entity.UserProfile;
import pers.project.api.security.model.vo.UserRegistryVO;
import pers.project.api.security.service.UserAccountService;

import static pers.project.api.common.constant.enumeration.ErrorEnum.DATABASE_ERROR;
import static pers.project.api.common.constant.enumeration.ErrorEnum.REGISTRY_ERROR;
import static pers.project.api.security.constant.AuthorityConst.ROLE_USER;


/**
 * 针对表【user_account (用户帐户) 】的数据库操作 Service 实现
 *
 * @author Luo Fei
 * @date 2023/03/23
 */
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
            String message = "Saving UserAccount error: %s, message: %s"
                    .formatted(userAccount, e.getMessage());
            throw new ServerException(DATABASE_ERROR, message);
        }
        // 保存用户资料
        UserProfile userProfile = new UserProfile();
        userProfile.setAccountId(userAccount.getId());
        userProfileMapper.insert(userProfile);
    }

}




