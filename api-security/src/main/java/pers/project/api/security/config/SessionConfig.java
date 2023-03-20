package pers.project.api.security.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;
import pers.project.api.security.SecurityApplication;

/**
 * @author Luo Fei
 * @version 2023/3/9
 */
@Configuration
public class SessionConfig {

    @Bean
    @ConditionalOnClass(SecurityApplication.class)
    public CookieSerializer cookieSerializer() {
        DefaultCookieSerializer defaultCookieSerializer = new DefaultCookieSerializer();
        // 默认情况 server.servlet.context-path
        defaultCookieSerializer.setCookiePath("/");
        return defaultCookieSerializer;
    }

}
