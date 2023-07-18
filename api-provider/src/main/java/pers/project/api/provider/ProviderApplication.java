package pers.project.api.provider;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

import java.nio.charset.Charset;
import java.time.ZoneId;

/**
 * Provider 主启动类
 *
 * @author Luo Fei
 * @date 2023/04/25
 */
@Slf4j
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class, ManagementWebSecurityAutoConfiguration.class})
public class ProviderApplication {

    public static void main(String[] args) {
        log.info("系统默认时区 {}，可使用 JVM 参数 -Duser.timezone=GMT+08:00 修改",
                ZoneId.systemDefault());
        log.info("系统默认字符集 {}，可使用 JVM 参数 -Dfile.encoding=UTF-8 修改",
                Charset.defaultCharset());
        SpringApplication.run(ProviderApplication.class, args);
    }

}
