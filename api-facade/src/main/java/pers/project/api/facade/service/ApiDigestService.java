package pers.project.api.facade.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pers.project.api.facade.model.po.ApiDigestPO;
import pers.project.api.facade.model.query.ApiDigestPageQuery;
import pers.project.api.facade.model.vo.ApiDigestPageVO;

/**
 * 针对表【api_digest (接口摘要) 】的数据库操作 Service
 *
 * @author Luo Fei
 * @date 2023/05/04
 */
public interface ApiDigestService extends IService<ApiDigestPO> {

    /**
     * 基于提供的 {@code ApiDigestPageQuery}，返回一个 {@code ApiDigestPageVO} 对象。
     *
     * @param pageQuery API 摘要分页 Query
     * @return API 摘要页面 VO
     */
    ApiDigestPageVO getApiDigestPageVO(ApiDigestPageQuery pageQuery);

}
