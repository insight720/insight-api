package pers.project.api.common.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Redisson 名称前缀常量
 *
 * @author Luo Fei
 * @date 2023/06/16
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RedissonNamePrefixConst {

    /**
     * 用户接口计数用法信号量名称前缀
     * <p>
     * 此名称前缀后还有两层目录结构：账户主键和接口摘要主键。
     * <p>
     * 具体格式为：
     * <pre>
     *     facade:usage:quantity:user:{accountId}:{digestId}
     * </pre>
     */
    public static final String USER_QUANTITY_USAGE_SEMAPHORE_KEY_PREFIX
            = "facade:usage:quantity:user:";

}
