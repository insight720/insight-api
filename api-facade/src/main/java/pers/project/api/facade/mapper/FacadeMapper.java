package pers.project.api.facade.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import pers.project.api.common.model.query.ApiAdminPageQuery;
import pers.project.api.common.model.vo.ApiAdminVO;

import java.util.List;

/**
 * @author Luo Fei
 * @date 2023/05/24
 */
@Mapper
public interface FacadeMapper {

    Long countApiAdminVOs(@Param("pageQuery") ApiAdminPageQuery pageQuery);

    List<ApiAdminVO> listApiAdminVOs(@Param("pageQuery") ApiAdminPageQuery pageQuery);

}
