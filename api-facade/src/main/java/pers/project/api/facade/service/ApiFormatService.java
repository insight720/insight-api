package pers.project.api.facade.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pers.project.api.facade.model.po.ApiFormatPO;
import pers.project.api.facade.model.vo.ApiFormatVO;

/**
 * 针对表【api_format (接口格式) 】的数据库操作 Service
 *
 * @author Luo Fei
 * @date 2023/05/04
 */
public interface ApiFormatService extends IService<ApiFormatPO> {

    /**
     * 根据指定的 API 摘要 ID，返回一个 {@code ApiFormatVO} 对象，
     * 该对象包含了对应 API 格式的详情信息。
     *
     * @param digestId API 摘要 ID
     * @return API 格式 VO
     */
    ApiFormatVO getApiFormatVO(String digestId);

}
