package pers.project.api.security.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import pers.project.api.common.model.entity.UserEntity;

/**
 * @author Luo Fei
 * @date 2023/03/05
 */
@Mapper
public interface UserMapper extends BaseMapper<UserEntity> {
}
