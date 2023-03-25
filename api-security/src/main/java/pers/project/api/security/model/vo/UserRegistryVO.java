package pers.project.api.security.model.vo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 用户注册 VO
 *
 * @author Luo Fei
 * @date 2023/03/22
 */
@Data
public class UserRegistryVO {

    // region For UserAccount
    /**
     * 账户名
     */
    @NotBlank
    @Size(min = 6, max = 12)
    private String username;
    /**
     * 密码
     */
    @NotBlank
    @Size(min = 8, max = 16)
    private String password;
    // endregion

    /**
     * 确认密码
     */
    @NotBlank
    @Size(min = 8, max = 16)
    private String confirmedPassword;

    // region For UserProfile
    // endregion

}
