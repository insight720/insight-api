package pers.project.api.facade.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pers.project.api.common.model.entity.ApiInfoEntity;

/**
 * 针对表【api_info (接口信息) 】的数据库操作 Service
 *
 * @author Luo Fei
 * @date 2023-02-22
 */
public interface ApiInfoService extends IService<ApiInfoEntity> {

    void validApiInfo(ApiInfoEntity apiInfoEntity, boolean add);

    /**
     * 从数据库中查询模拟接口是否存在（请求路径、请求方法、请求参数）
     */
    ApiInfoEntity getApiInfo(String url, String method);

}
