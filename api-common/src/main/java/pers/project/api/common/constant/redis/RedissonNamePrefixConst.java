package pers.project.api.common.constant.redis;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;

/**
 * {@link RedissonClient} 名称前缀常量
 * <p>
 * 名称前缀命名规则参考：
 * <pre>
 * 业务域所属的项目:业务域:数据内容_数据类型:
 * </pre>
 *
 * @author Luo Fei
 * @date 2023/06/16
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RedissonNamePrefixConst {

    /**
     * 用户接口计数用法存量的 {@link RSemaphore} 名称前缀
     * <p>
     * 此名称前缀后还有一层目录结构：用户接口计数用法主键。
     * <p>
     * 具体格式为：
     * <pre>
     * facade:quantity_usage:user_quantity_usage_stock_semaphore:{usageId}
     * </pre>
     */
    public static final String USER_QUANTITY_USAGE_STOCK_SEMAPHORE_NAME_PREFIX
            = "facade:quantity_usage:user_quantity_usage_stock_semaphore:";

    /**
     * 用户接口计数用法总调用次数的 {@link RSemaphore} 名称前缀
     * <p>
     * 此名称前缀后还有一层目录结构：用户接口计数用法主键。
     * <p>
     * 具体格式为：
     * <pre>
     * facade:quantity_usage:user_quantity_usage_total_semaphore:{usageId}
     * </pre>
     */
    public static final String USER_QUANTITY_USAGE_TOTAL_SEMAPHORE_NAME_PREFIX
            = "facade:quantity_usage:user_quantity_usage_total_semaphore:";

    /**
     * 用户接口计数用法失败调用次数的 {@link RSemaphore} 名称前缀
     * <p>
     * 此名称前缀后还有一层目录结构：用户接口计数用法主键。
     * <p>
     * 具体格式为：
     * <pre>
     * facade:quantity_usage:user_quantity_usage_failure_semaphore:{usageId}
     * </pre>
     */
    public static final String USER_QUANTITY_USAGE_FAILURE_SEMAPHORE_NAME_PREFIX
            = "facade:quantity_usage:user_quantity_usage_failure_semaphore:";

    /**
     * 接口计数用法总调用次数的 {@link RSemaphore} 名称前缀
     * <p>
     * 此名称前缀后还有一层目录结构：接口摘要主键。
     * <p>
     * 具体格式为：
     * <pre>
     * facade:quantity_usage:user_quantity_usage_total_semaphore:{usageId}
     * </pre>
     */
    public static final String API_QUANTITY_USAGE_TOTAL_SEMAPHORE_NAME_PREFIX
            = "facade:quantity_usage:api_quantity_usage_total_semaphore:";

    /**
     * 接口计数用法失败调用次数的 {@link RSemaphore} 名称前缀
     * <p>
     * 此名称前缀后还有一层目录结构：接口摘要主键。
     * <p>
     * 具体格式为：
     * <pre>
     * facade:quantity_usage:user_quantity_usage_failure_semaphore:{digestId}
     * </pre>
     */
    public static final String API_QUANTITY_USAGE_FAILURE_SEMAPHORE_NAME_PREFIX
            = "facade:quantity_usage:api_quantity_usage_failure_semaphore:";

}
