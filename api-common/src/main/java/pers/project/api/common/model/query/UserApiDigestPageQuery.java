package pers.project.api.common.model.query;

import lombok.Data;
import pers.project.api.common.validation.constraint.SnowflakeId;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * 用户 API 摘要分页 Query
 *
 * @author Luo Fei
 * @date 2023/05/04
 */
@Data
public class UserApiDigestPageQuery {

    /**
     * 账户主键
     */
    @SnowflakeId
    private String accountId;

    /**
     * 接口名称（模糊查询）
     */
    private String apiName;

    /**
     * 接口描述（模糊查询）
     */
    private String description;

    /**
     * 请求方法集合
     */
    private Set<Integer> method;

    /**
     * 接口地址（模糊查询）
     */
    private String url;

    /**
     * 接口状态集合
     */
    private Set<Integer> apiStatus;

    /**
     * 创建时间范围
     * <p>
     * [0] 起始时间
     * <p>
     * [1] 终止时间（包括）
     */
    private LocalDateTime[] createTime;

    /**
     * 更新时间范围
     * <p>
     * [0] 起始时间
     * <p>
     * [1] 终止时间（包括）
     */
    private LocalDateTime[] updateTime;

    /**
     * 每页显示条数
     */
    private Long size;

    /**
     * 当前页
     */
    private Long current;

}
