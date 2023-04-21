package pers.project.api.security.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import static pers.project.api.security.enumeration.UploadStrategyEnum.COS;

/**
 * 上传文件枚举
 *
 * @author Luo Fei
 * @date 2023/04/13
 */
@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
public enum UploadFileEnum {

    AVATAR(COS, "profile/avatar/", "用户的头像");

    /**
     * 上传策略枚举
     *
     * @see UploadFileEnum
     */
    private final UploadStrategyEnum uploadStrategyEnum;

    /**
     * 上传目录 URI
     * <p>
     * 以分隔符 / 结尾，不以分隔符 / 开始。
     */
    private final String directoryUri;

    /**
     * 上传文件的描述
     */
    private final String description;

}
