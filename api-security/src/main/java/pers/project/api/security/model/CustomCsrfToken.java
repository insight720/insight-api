package pers.project.api.security.model;

import lombok.Data;

/**
 * 自定义 CSRF 令牌
 *
 * @author Luo Fei
 * @date 2023/03/23
 */
@Data
public class CustomCsrfToken {

    /**
     * 令牌值
     */
    private String tokenValue;

}
