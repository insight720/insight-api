package pers.project.api.security.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import pers.project.api.common.model.query.UserAdminPageQuery;
import pers.project.api.common.model.vo.UserAdminVO;

import java.util.List;

/**
 * Security 模块的数据库操作 Mapper
 *
 * @author Luo Fei
 * @date 2023/05/21
 */
@Mapper
public interface SecurityMapper {

    Long countUserAdminVOs(@Param("pageQuery") UserAdminPageQuery pageQuery);

    List<UserAdminVO> listUserAdminVOs(@Param("pageQuery") UserAdminPageQuery pageQuery);

}
