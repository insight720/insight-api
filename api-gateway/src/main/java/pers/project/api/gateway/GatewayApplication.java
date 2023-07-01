package pers.project.api.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.security.reactive.ReactiveManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveUserDetailsServiceAutoConfiguration;

/**
 * Gateway 主启动类
 *
 * @author Luo Fei
 * @date 2023/04/25
 */
@SpringBootApplication
        // TODO: 2023/7/2 是否有必要使用 Spring Security 依赖
        (exclude = {ReactiveManagementWebSecurityAutoConfiguration.class, ReactiveSecurityAutoConfiguration.class, ReactiveUserDetailsServiceAutoConfiguration.class})
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }

}
