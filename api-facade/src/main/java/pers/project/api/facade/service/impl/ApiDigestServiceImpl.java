package pers.project.api.facade.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import pers.project.api.facade.mapper.ApiDigestMapper;
import pers.project.api.facade.model.entity.ApiDigest;
import pers.project.api.facade.service.ApiDigestService;

/**
 * 针对表【api_digest (接口摘要) 】的数据库操作 Service 实现
 *
 * @author Luo Fei
 * @date 2023/03/20
 */
@Service
public class ApiDigestServiceImpl extends ServiceImpl<ApiDigestMapper, ApiDigest> implements ApiDigestService {

}




