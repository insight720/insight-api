package pers.project.api.facade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import pers.project.api.facade.model.entity.UserEntity;

import java.util.List;

/**
 * 针对表【user_api_info (用户调用接口关系) 】的数据库操作 Mapper
 *
 * @author Luo Fei
 * @version 2023-02-27
 */
@Mapper
public interface UserApiInfoMapper extends BaseMapper<UserEntity> {

    List<UserEntity> listTopInvokeApiInfo(@Param("limit") int limit);

}




