package pers.project.api.facade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import pers.project.api.facade.model.po.ApiDigestPo;

/**
 * 针对表【api_digest (接口摘要) 】的数据库操作 Mapper
 *
 * @author Luo Fei
 * @date 2023/05/04
 */
@Mapper
public interface ApiDigestMapper extends BaseMapper<ApiDigestPo> {

}




