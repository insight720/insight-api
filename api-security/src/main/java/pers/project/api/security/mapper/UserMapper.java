package pers.project.api.security.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import pers.project.api.common.model.entity.UserEntity;

/**
 * @author Luo Fei
 * @version 2023/3/5
 */
@Mapper
public interface UserMapper extends BaseMapper<UserEntity> {
}
