package pers.project.api.common.model.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

/**
 * 自定义 GrantedAuthority
 * <p>
 * 与 {@link SimpleGrantedAuthority} 类似，但没有 JSON 序列化问题。
 *
 * @author Luo Fei
 * @date 2023/03/21
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomizedGrantedAuthority implements GrantedAuthority {

    private String role;

    @Override
    public String getAuthority() {
        return this.role;
    }

}
