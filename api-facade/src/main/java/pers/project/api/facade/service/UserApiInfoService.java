package pers.project.api.facade.service;


import com.baomidou.mybatisplus.extension.service.IService;
import pers.project.api.facade.model.entity.UserEntity;

/**
 * 针对表【user_api_info (用户调用接口关系) 】的数据库操作 Service
 *
 * @author Luo Fei
 * @date 2023-02-27
 */
public interface UserApiInfoService extends IService<UserEntity> {


    void validUserApiInfo(UserEntity userEntity, boolean save);


    /**
     * 调用接口统计
     *
     * @param apiInfoId
     * @param userId
     * @return
     */
    boolean invokeCount(long apiInfoId, long userId);

}
