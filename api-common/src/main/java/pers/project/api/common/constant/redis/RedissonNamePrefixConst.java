package pers.project.api.common.constant.redis;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
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


}
