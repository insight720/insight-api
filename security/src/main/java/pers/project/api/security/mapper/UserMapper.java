package pers.project.api.security.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import pers.project.api.security.model.entity.User;

/**
 * @author Luo Fei
 * @date 2023/3/5
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
