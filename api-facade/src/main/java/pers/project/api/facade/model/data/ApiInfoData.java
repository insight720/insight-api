package pers.project.api.facade.model.data;

import lombok.Data;
import lombok.EqualsAndHashCode;
import pers.project.api.common.model.entity.ApiInfoEntity;

/**
 * 接口信息封装视图
 *
 * @author yupi
 * @TableName product
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ApiInfoData extends ApiInfoEntity {

    /**
     * 调用次数
     */
    private Integer totalNum;

    private static final long serialVersionUID = 1L;
}