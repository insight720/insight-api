package pers.project.api.security.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import pers.project.api.security.mapper.UserProfileMapper;
import pers.project.api.security.model.entity.UserProfile;
import pers.project.api.security.service.UserProfileService;

/**
 * 针对表【user_profile (用户资料) 】的数据库操作 Service 实现
 *
 * @author Luo Fei
 * @date 2023/03/20
 */
@Service
public class UserProfileServiceImpl extends ServiceImpl<UserProfileMapper, UserProfile> implements UserProfileService {

}




