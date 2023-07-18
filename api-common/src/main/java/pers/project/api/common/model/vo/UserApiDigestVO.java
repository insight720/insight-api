package pers.project.api.common.model.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * 用户 API 摘要 VO
 *
 * @author Luo Fei
 * @date 2023/05/04
 */
@Data
public class UserApiDigestVO {

    /**
     * 请求方法集合
     */
    private Set<String> methodSet;

    /**
     * 接口用法类型集合
     */
    private Set<String> usageTypeSet;

    // region Same with ApiDigestPO
    /**
     * 接口摘要主键
     */
    private String digestId;

    /**
     * 创建账户主键
     */
    private String accountId;

    /**
     * 接口名称
     */
    private String apiName;

    /**
     * 接口描述
     */
    private String description;

    /**
     * 接口地址
     */
    private String url;

    /**
     * 接口状态
     */
    private Integer apiStatus;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    // endregion

}
