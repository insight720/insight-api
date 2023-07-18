package pers.project.api.gateway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.security.reactive.ReactiveManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveUserDetailsServiceAutoConfiguration;

import java.nio.charset.Charset;
import java.time.ZoneId;

/**
 * Gateway 主启动类
 *
 * @author Luo Fei
 * @date 2023/04/25
 */
@Slf4j
@SpringBootApplication
        // TODO: 2023/7/2 是否有必要使用 Spring Security 依赖
        (exclude = {ReactiveManagementWebSecurityAutoConfiguration.class, ReactiveSecurityAutoConfiguration.class, ReactiveUserDetailsServiceAutoConfiguration.class})
public class GatewayApplication {

    public static void main(String[] args) {
        log.info("系统默认时区 {}，可使用 JVM 参数 -Duser.timezone=GMT+08:00 修改",
                ZoneId.systemDefault());
        log.info("系统默认字符集 {}，可使用 JVM 参数 -Dfile.encoding=UTF-8 修改",
                Charset.defaultCharset());
        SpringApplication.run(GatewayApplication.class, args);
    }

}
