package pers.project.api.facade;

import com.alibaba.fastjson2.support.spring6.data.redis.GenericFastJsonRedisSerializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.session.FlushMode;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

import java.nio.charset.Charset;
import java.time.ZoneId;

@Slf4j
@EnableRedisHttpSession(flushMode = FlushMode.IMMEDIATE)
@EnableDiscoveryClient
@SpringBootApplication
public class FacadeApplication {

    public static void main(String[] args) {
        log.info("系统默认时区 {}，可使用 JVM 参数 -Duser.timezone=GMT+08:00 修改",
                ZoneId.systemDefault());
        log.info("系统默认字符集 {}，可使用 JVM 参数 -Dfile.encoding=UTF-8 修改",
                Charset.defaultCharset());
        SpringApplication.run(FacadeApplication.class, args);
    }

    @Bean
    @Qualifier("springSessionDefaultRedisSerializer")
    public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
        return new GenericFastJsonRedisSerializer();
    }


}
