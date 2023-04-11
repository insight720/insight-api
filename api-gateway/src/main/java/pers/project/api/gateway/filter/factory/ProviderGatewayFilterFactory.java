package pers.project.api.gateway.filter.factory;

import jakarta.annotation.Resource;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Provider 网关过滤器工厂
 *
 * @author Luo Fei
 * @date 2023/03/10
 * @see <a href="https://springdoc.cn/spring-cloud-gateway/#%E7%BC%96%E5%86%99%E8%87%AA%E5%AE%9A%E4%B9%89-gatewayfilter-%E5%B7%A5%E5%8E%82">
 * 编写自定义 GatewayFilter 工厂</a>
 */
@Slf4j
@Component
public class ProviderGatewayFilterFactory extends AbstractGatewayFilterFactory<ProviderGatewayFilterFactory.Config> {

    /**
     * 配置类
     * <p>
     * Config 类的字段是过滤器在配置文件中的属性。
     * <p>
     * 注意：如果有多个字段，请按字段名的字典顺序排列。
     */
    @Getter
    @Setter
    @Component
    public static class Config {
    }

    @Resource
    private GatewayFilter providerGatewayFilter;

    public ProviderGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public List<String> shortcutFieldOrder() {
        return Arrays.stream(Config.class.getDeclaredFields())
                .map(Field::getName)
                .sorted(Comparator.naturalOrder())
                .collect(Collectors.toList());
    }

    @Override
    public GatewayFilter apply(Config config) {
        return providerGatewayFilter;
    }

}

