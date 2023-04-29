package pers.project.api.common.model.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.util.Assert;

import java.io.Serial;
import java.util.Objects;

/**
 * 自定义 GrantedAuthority
 * <p>
 * 与 {@link SimpleGrantedAuthority} 类似，但没有 JSON 序列化问题。
 *
 * @author Luo Fei
 * @date 2023/03/21
 */
public class CustomGrantedAuthority implements GrantedAuthority {

    @Serial
    private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

    private String authority;

    /**
     * 不要使用它，仅为防止 JSON 序列化问题。
     */
    public CustomGrantedAuthority() {
    }

    public CustomGrantedAuthority(String authority) {
        Assert.hasText(authority, "A granted authority textual representation is required");
        this.authority = authority;
    }

    @Override
    public String getAuthority() {
        return this.authority;
    }

    public void setAuthority(String authority) {
        Assert.hasText(authority, "A granted authority textual representation is required");
        this.authority = authority;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        CustomGrantedAuthority that = (CustomGrantedAuthority) obj;
        return Objects.equals(authority, that.authority);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(authority);
    }

}
