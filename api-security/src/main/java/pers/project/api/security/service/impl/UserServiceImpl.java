package pers.project.api.security.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import pers.project.api.common.constant.enumeration.ErrorEnum;
import pers.project.api.common.exception.ServiceException;
import pers.project.api.common.model.entity.UserEntity;
import pers.project.api.security.mapper.UserMapper;
import pers.project.api.security.service.UserService;

import static pers.project.api.common.constant.UserConst.USER_LOGIN_STATE;


/**
 * 针对表【user (用户) 】的数据库操作 Service 实现
 *
 * @author Luo Fei
 * @version 2023-02-25
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserEntity> implements UserService {

    @Resource
    private UserMapper userMapper;

    /**
     * 盐值，混淆密码
     */
    private static final String SALT = "yupi";

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new ServiceException(ErrorEnum.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new ServiceException(ErrorEnum.PARAMS_ERROR, "用户账号过短");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new ServiceException(ErrorEnum.PARAMS_ERROR, "用户密码过短");
        }
        // 密码和校验密码相同
        if (!userPassword.equals(checkPassword)) {
            throw new ServiceException(ErrorEnum.PARAMS_ERROR, "两次输入的密码不一致");
        }
        synchronized (userAccount.intern()) {
            // 账户不能重复
            LambdaQueryWrapper<UserEntity> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(UserEntity::getUserAccount, userAccount);
            long count = userMapper.selectCount(queryWrapper);
            if (count > 0) {
                throw new ServiceException(ErrorEnum.PARAMS_ERROR, "账号重复");
            }
            // 2. 加密
            String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
            // 3. 插入数据
            UserEntity userEntity = new UserEntity();
            userEntity.setUserAccount(userAccount);
            userEntity.setUserPassword(encryptPassword);
            // TODO: 2023/1/20 自定义签名认证
            userEntity.setAccessKey("default");
            userEntity.setSecretKey("default");
            boolean saveResult = this.save(userEntity);
            if (!saveResult) {
                throw new ServiceException(ErrorEnum.SYSTEM_ERROR, "注册失败，数据库错误");
            }
            return userEntity.getId();
        }
    }

    @Override
    public UserEntity userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new ServiceException(ErrorEnum.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new ServiceException(ErrorEnum.PARAMS_ERROR, "账号错误");
        }
        if (userPassword.length() < 8) {
            throw new ServiceException(ErrorEnum.PARAMS_ERROR, "密码错误");
        }
        // 2. 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 查询用户是否存在
        LambdaQueryWrapper<UserEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserEntity::getUserAccount, userAccount);
        queryWrapper.eq(UserEntity::getUserPassword, encryptPassword);
        UserEntity userEntity = userMapper.selectOne(queryWrapper);
        // 用户不存在
        if (userEntity == null) {
            log.info("user login failed, userAccount cannot match userPassword");
            throw new ServiceException(ErrorEnum.PARAMS_ERROR, "用户不存在或密码错误");
        }
        // 3. 记录用户的登录态
        HttpSession session = request.getSession();
        System.out.println("session.getId() = " + session.getId());
        session.setAttribute(USER_LOGIN_STATE, userEntity);
        return userEntity;
    }

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    @Override
    public UserEntity getLoginUser(HttpServletRequest request) {
        // 先判断是否已登录
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        UserEntity currentUserEntity = (UserEntity) userObj;
        if (currentUserEntity == null || currentUserEntity.getId() == null) {
            throw new ServiceException(ErrorEnum.NOT_LOGIN_ERROR);
        }
        // 从数据库查询（追求性能的话可以注释，直接走缓存）
        long userId = currentUserEntity.getId();
        currentUserEntity = this.getById(userId);
        if (currentUserEntity == null) {
            throw new ServiceException(ErrorEnum.NOT_LOGIN_ERROR);
        }
        return currentUserEntity;
    }


    /**
     * 用户注销
     *
     * @param request
     */
    @Override
    public boolean userLogout(HttpServletRequest request) {
        if (request.getSession().getAttribute(USER_LOGIN_STATE) == null) {
            throw new ServiceException(ErrorEnum.OPERATION_ERROR, "未登录");
        }
        // 移除登录态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return true;
    }

    @Override
    public UserEntity getInvokeUser(String accessKey) {
        if (StringUtils.isAnyBlank(accessKey)) {
            throw new ServiceException(ErrorEnum.PARAMS_ERROR);
        }
        QueryWrapper<UserEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("access_key", accessKey);
        UserEntity userEntity = userMapper.selectOne(queryWrapper);
        return userEntity;
    }

}




