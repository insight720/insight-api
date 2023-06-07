package pers.project.api.facade.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pers.project.api.facade.model.po.ApiQuantityUsagePO;
import pers.project.api.facade.model.vo.ApiQuantityUsageVO;
import pers.project.api.facade.model.vo.ApiStockInfoVO;

/**
 * 针对表【api_quantity_usage (接口计数用法) 】的数据库操作 Service
 *
 * @author Luo Fei
 * @date 2023/05/04
 */
public interface ApiQuantityUsageService extends IService<ApiQuantityUsagePO> {

    /**
     * 根据指定的 API 摘要 ID，返回一个 {@code ApiQuantityUsageVO} 对象，
     * 该对象包含了对应 API 的计数用法信息。
     *
     * @param digestId API 摘要 ID
     * @return 接口计数用法 VO
     */
    ApiQuantityUsageVO getApiQuantityUsageVO(String digestId);

    /**
     * 根据指定的 API 摘要 ID，返回一个 {@code ApiStockInfoVO} 对象，
     * 该对象包含了对应 API 的库存信息。
     *
     * @param digestId API 摘要 ID
     * @return 接口库存信息 VO
     */
    ApiStockInfoVO getApiStockInfoVO(String digestId);

}
