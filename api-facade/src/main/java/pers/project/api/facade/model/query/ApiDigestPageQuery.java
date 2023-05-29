package pers.project.api.facade.model.query;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import pers.project.api.common.validation.constraint.ContainedIn;
import pers.project.api.common.validation.constraint.NullOrNotBlank;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * API 摘要分页 Query
 *
 * @author Luo Fei
 * @date 2023/05/19
 */
@Data
public class ApiDigestPageQuery {

    // region For Pagination
    /**
     * 每页显示条数
     */
    @NotNull
    @Positive
    private Long size;

    /**
     * 当前页
     */
    @NotNull
    @Positive
    private Long current;
    // endregion

    // region From ApiDigestPO
    /**
     * 接口名称（模糊查询）
     */
    @NullOrNotBlank
    private String apiName;

    /**
     * 接口描述（模糊查询）
     */
    @NullOrNotBlank
    private String description;

    /**
     * 请求方法集合
     */
    @Size(min = 1)
    private Set<@ContainedIn(values = {"GET"}) String> methodSet;

    /**
     * 接口地址（模糊查询）
     */
    @NullOrNotBlank
    private String url;

    /**
     * 接口用法类型集合
     */
    @Size(min = 1)
    private Set<String> usageTypeSet;

    /**
     * 接口状态集合
     */
    @Size(min = 1)
    private Set<Integer> apiStatusSet;

    /**
     * 创建时间范围
     * <p>
     * [0] 起始时间
     * <p>
     * [1] 终止时间（包括）
     */
    @Size(min = 2, max = 2)
    private LocalDateTime[] createTimeRange;

    /**
     * 更新时间范围
     * <p>
     * [0] 起始时间
     * <p>
     * [1] 终止时间（包括）
     */
    @Size(min = 2, max = 2)
    private LocalDateTime[] updateTimeRange;
    // endregion

}
