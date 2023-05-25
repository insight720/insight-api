package pers.project.api.common.model.vo;

import lombok.Data;

import java.util.List;

/**
 * 用户管理页面 VO
 *
 * @author Luo Fei
 * @date 2023/05/20
 */
@Data
public class UserAdminPageVO {

    /**
     * 符合条件的用户总数
     */
    private Long total;

    /**
     * 当前页面的用户管理信息列表
     */
    private List<UserAdminVO> userAdminVOList;

}
