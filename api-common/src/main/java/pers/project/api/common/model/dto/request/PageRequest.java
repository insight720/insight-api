package pers.project.api.common.model.dto.request;

import lombok.Data;
import pers.project.api.common.constant.CommonConst;

/**
 * 分页请求
 *
 * @author yupi
 */
@Data
public class PageRequest {

    /**
     * 当前页号
     */
    private Long current = 1L;

    /**
     * 页面大小
     */
    private Long pageSize = 10L;

    /**
     * 排序字段
     */
    private String sortField;

    /**
     * 排序顺序（默认升序）
     */
    private String sortOrder = CommonConst.SORT_ORDER_ASC;

}
