package pers.project.api.security.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import pers.project.api.security.model.entity.UserApiInfo;

import java.util.List;

/**
 * 针对表【user_api_info (用户调用接口关系) 】的数据库操作 Mapper
 *
 * @author Luo Fei
 * @date 2023-02-27
 */
@Mapper
public interface UserApiInfoMapper extends BaseMapper<UserApiInfo> {

    List<UserApiInfo> listTopInvokeApiInfo(@Param("limit") int limit);

}




