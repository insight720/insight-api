package pers.project.api.common.model.vo;

import lombok.Data;

import java.util.List;

/**
 * 接口管理页面 VO
 *
 * @author Luo Fei
 * @date 2023/05/23
 */
@Data
public class ApiAdminPageVO {

    /**
     * 符合条件的接口总数
     */
    private Long total;

    /**
     * 当前页面的接口管理信息列表
     */
    private List<ApiAdminVO> apiAdminVOList;

}
