package pers.project.api.security.model.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import pers.project.api.common.model.entity.ApiInfo;

/**
 * 接口信息封装视图
 *
 * @author yupi
 * @TableName product
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ApiInfoVO extends ApiInfo {

    /**
     * 调用次数
     */
    private Integer totalNum;

    private static final long serialVersionUID = 1L;
}