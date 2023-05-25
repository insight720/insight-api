package pers.project.api.common.model.query;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * API 管理分页 Query
 *
 * @author Luo Fei
 * @date 2023/05/23
 */
@Data
public class ApiAdminPageQuery {

    /**
     * 每页显示条数
     */
    private Long size;

    /**
     * 当前页
     */
    private Long current;

    /**
     * 接口用法类型集合
     */
    private Set<String> usageTypeSet;

    /**
     * 接口摘要更新时间范围
     *
     * <p>
     * [0] 起始时间
     * <p>
     * [1] 终止时间（包括）
     */
    private LocalDateTime[] digestUpdateTimeRange;

    /**
     * 接口格式主键
     */
    private String formatId;

    /**
     * 请求方法集合
     */
    private Set<Integer> methodSet;

    /**
     * 接口格式更新时间范围
     *
     * <p>
     * [0] 起始时间
     * <p>
     * [1] 终止时间（包括）
     */
    private LocalDateTime[] formatUpdateTimeRange;

    /**
     * 接口状态集合
     */
    private Set<Integer> apiStatusSet;

    /**
     * 创建时间范围
     * <p>
     * [0] 起始时间
     * <p>
     * [1] 终止时间（包括）
     */
    private LocalDateTime createTimeRange;

    // region Same with ApiDigestPo and ApiFormatPo
    /**
     * 是否删除（1 表示删除，0 表示未删除）
     */
    private Integer isDeleted;
    // endregion

    // region Same with ApiDigestPo
    /**
     * 创建账户主键
     */
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
     * 接口地址（模糊查询）
     */
    private String url;
    // endregion

    // region Same with ApiFormatPo
    /**
     * 接口摘要主键
     */
    private String digestId;

    /**
     * 请求参数（模糊查询）
     */
    private String requestParam;

    /**
     * 请求头（模糊查询）
     */
    private String requestHeader;

    /**
     * 请求体（模糊查询）
     */
    private String requestBody;

    /**
     * 响应头（模糊查询）
     */
    private String responseHeader;

    /**
     * 响应体（模糊查询）
     */
    private String responseBody;
    // endregion

}
