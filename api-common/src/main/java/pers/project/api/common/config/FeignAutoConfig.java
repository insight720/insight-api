package pers.project.api.common.config;

import feign.RequestInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Feign 自动配置
 *
 * @author Luo Fei
 * @date 2023/03/15
 */
@AutoConfiguration
@EnableFeignClients(basePackages = "pers.project.api")
public class FeignAutoConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        RequestInterceptor requestInterceptor = requestTemplate ->
        {
            // TODO: 2023/03/20 新线程发起的请求会丢失上下文信息
//             用 RequestContextHolder.setRequestAttributes();
            // 1. feign 调用前的原始请求
            ServletRequestAttributes servletRequestAttributes
                    = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (servletRequestAttributes != null) {
                HttpServletRequest httpServletRequest = servletRequestAttributes.getRequest();
                // 2. 参数同步到 feign 请求
                String cookie = httpServletRequest.getHeader("Cookie");
                requestTemplate.header("Cookie", cookie);
            }
        };
        return requestInterceptor;
    }

}
