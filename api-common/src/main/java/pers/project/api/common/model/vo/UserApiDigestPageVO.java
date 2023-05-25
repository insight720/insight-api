package pers.project.api.common.model.vo;

import lombok.Data;

import java.util.List;

/**
 * 用户 API 摘要页面 VO
 *
 * @author Luo Fei
 * @date 2023/05/06
 */
@Data
public class UserApiDigestPageVO {

    /**
     * API 摘要信息的总数
     */
    private Long total;

    /**
     * 当前页面的 API 摘要信息列表
     */
    private List<UserApiDigestVO> digestVOList;

}
