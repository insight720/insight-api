package pers.project.api.security.mapper;

import pers.project.api.security.model.entity.ApiInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 针对表【api_info (接口信息) 】的数据库操作 Mapper
 *
 * @author Luo Fei
 * @date 2023-02-22
 */
@Mapper
public interface ApiInfoMapper extends BaseMapper<ApiInfo> {

}




