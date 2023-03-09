package pers.project.api.security.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import pers.project.api.security.model.entity.Post;

/**
 * @author yupili
 * @description 针对表【post(帖子)】的数据库操作Mapper
 * @createDate 2022-09-13 16:03:41
 * @Entity com.yupi.project.model.entity.Post
 */
@Mapper
public interface PostMapper extends BaseMapper<Post> {

}




