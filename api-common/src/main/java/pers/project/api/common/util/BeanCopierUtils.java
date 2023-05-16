package pers.project.api.common.util;


import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.cglib.core.Converter;
import pers.project.api.common.bean.IgnoreNullConverter;


/**
 * BeanCopier 工具类
 *
 * @author Luo Fei
 * @date 2023/03/15
 */
public abstract class BeanCopierUtils {

    private static final LoadingCache<BeanCopierId, BeanCopier>
            BEAN_COPIER_CACHE = Caffeine.newBuilder().build
            (id -> BeanCopier.create(id.source, id.target, id.useConverter));

    private record BeanCopierId(Class<?> source, Class<?> target, boolean useConverter) {
    }

    /**
     * 将给定源 Bean 的属性值复制到目标 Bean 中。
     * <p>
     * 注意：源 Bean 属性和目标 Bean 属性的名称及类型必须匹配，并且源 Bean
     * 属性要有 Getter 方法，目标 Bean 属性要有 Setter 方法。源 Bean
     * 公开但目标 Bean 未公开的任何 Bean 属性都将被静默忽略。
     *
     * @param source 源 Bean
     * @param target 目标 Bean
     */
    public static void copy(Object source, Object target) {
        BeanCopierUtils.copy(source, target, null);
    }

    /**
     * 将给定源 Bean 的属性值复制到目标 Bean 中。
     * <p>
     * 在 {@link BeanCopierUtils#copy(Object, Object)} 方法的基础上，Bean 属性复制结果经过
     * Converter 调整。
     *
     * @param source    源 Bean
     * @param target    目标 Bean
     * @param converter Bean 属性转换器
     */
    public static void copy(Object source, Object target, Converter converter) {
        BeanCopierId copierId = new BeanCopierId
                (source.getClass(), target.getClass(), converter != null);
        BeanCopier beanCopier = BEAN_COPIER_CACHE.get(copierId);
        beanCopier.copy(source, target, converter);
    }

    /**
     * 将给定源 Bean 的属性值复制到目标 Bean 中。
     * <p>
     * 在 {@link BeanCopierUtils#copy(Object, Object)} 方法的基础上，不复制源 Bean 中的 null 属性。
     *
     * @param source 源 Bean
     * @param target 目标 Bean
     */
    public static void copyIgnoreNull(Object source, Object target) {
        BeanCopierUtils.copy(source, target, new IgnoreNullConverter(target));
    }

}


