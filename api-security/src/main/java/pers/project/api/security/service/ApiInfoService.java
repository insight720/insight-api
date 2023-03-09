package pers.project.api.security.service;

import pers.project.api.security.model.entity.ApiInfo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 针对表【api_info (接口信息) 】的数据库操作 Service
 *
 * @author Luo Fei
 * @date 2023-02-22
 */
public interface ApiInfoService extends IService<ApiInfo> {

    void validApiInfo(ApiInfo apiInfo, boolean add);

    /**
     * 从数据库中查询模拟接口是否存在（请求路径、请求方法、请求参数）
     */
    ApiInfo getApiInfo(String url, String method);

}
