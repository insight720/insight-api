package pers.project.api.facade;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

import java.nio.charset.Charset;
import java.time.ZoneId;

@Slf4j
@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class FacadeApplication {

    public static void main(String[] args) {
        log.info("系统默认时区 {}，可使用 JVM 参数 -Duser.timezone=GMT+08:00 修改",
                ZoneId.systemDefault());
        log.info("系统默认字符集 {}，可使用 JVM 参数 -Dfile.encoding=UTF-8 修改",
                Charset.defaultCharset());
        SpringApplication.run(FacadeApplication.class, args);
    }

}
