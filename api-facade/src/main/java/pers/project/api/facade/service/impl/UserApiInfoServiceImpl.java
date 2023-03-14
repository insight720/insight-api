package pers.project.api.facade.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import pers.project.api.common.enums.ErrorCodeEnum;
import pers.project.api.common.exception.ServiceException;
import pers.project.api.facade.mapper.UserApiInfoMapper;
import pers.project.api.facade.model.entity.UserApiInfo;
import pers.project.api.facade.service.UserApiInfoService;


/**
 * 针对表【user_api_info (用户调用接口关系) 】的数据库操作 Service 实现
 *
 * @author Luo Fei
 * @date 2023-02-27
 */
@Service
public class UserApiInfoServiceImpl extends ServiceImpl<UserApiInfoMapper, UserApiInfo> implements UserApiInfoService {

    @Override
    public void validUserApiInfo(UserApiInfo userApiInfo, boolean save) {

    }

    @Override
    public boolean invokeCount(long apiInfoId, long userId) {
        // 判断
        if (apiInfoId <= 0 || userId <= 0) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_ERROR);
        }
        UpdateWrapper<UserApiInfo> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("api_info_id", apiInfoId);
        updateWrapper.eq("user_id", userId);
        updateWrapper.gt("left_num", 0);
        updateWrapper.setSql("left_num = left_num - 1, total_num = total_num + 1");
        return this.update(updateWrapper);
    }

}




