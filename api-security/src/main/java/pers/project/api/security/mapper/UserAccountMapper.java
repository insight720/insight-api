package pers.project.api.security.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import pers.project.api.security.model.po.UserAccountPO;

/**
 * 针对表【user_account (用户帐户) 】的数据库操作 Mapper
 *
 * @author Luo Fei
 * @date 2023/03/23
 */
@Mapper
public interface UserAccountMapper extends BaseMapper<UserAccountPO> {

}




