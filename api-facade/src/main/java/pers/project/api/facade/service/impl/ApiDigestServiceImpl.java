package pers.project.api.facade.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import pers.project.api.facade.mapper.ApiDigestMapper;
import pers.project.api.facade.model.po.ApiDigestPo;
import pers.project.api.facade.service.ApiDigestService;

/**
 * 针对表【api_digest (接口摘要) 】的数据库操作 Service 实现
 *
 * @author Luo Fei
 * @date 2023/05/04
 */
@Service
public class ApiDigestServiceImpl extends ServiceImpl<ApiDigestMapper, ApiDigestPo> implements ApiDigestService {

}




