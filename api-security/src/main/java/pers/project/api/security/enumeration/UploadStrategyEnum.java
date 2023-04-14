package pers.project.api.security.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 上传策略枚举
 *
 * @author Luo Fei
 * @date 2023/04/14
 */
@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
public enum UploadStrategyEnum {

    /**
     * 腾讯云对象存储
     */
    COS("cosUploadStrategy");

    /**
     * 上传策略实现类的 Bean 名称
     * <p>
     * 默认为首字母小写的上传策略实现类名。
     */
    private final String beanName;

}
