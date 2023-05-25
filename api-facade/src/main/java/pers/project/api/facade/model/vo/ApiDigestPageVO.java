package pers.project.api.facade.model.vo;

import lombok.Data;

import java.util.List;

/**
 * API 摘要页面 VO
 *
 * @author Luo Fei
 * @date 2023/05/19
 */
@Data
public class ApiDigestPageVO {

    /**
     * API 摘要信息的总数
     */
    private Long total;

    /**
     * 当前页面的 API 摘要信息列表
     */
    private List<ApiDigestVO> digestVOList;

}
