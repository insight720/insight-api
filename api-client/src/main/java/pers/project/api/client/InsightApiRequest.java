package pers.project.api.client;

import pers.project.api.client.impl.InsightApiRequestImpl;

import java.io.InputStream;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;

/**
 * Insight API 的请求接口
 *
 * @author Luo Fei
 * @date 2023/07/11
 */
@SuppressWarnings("unused")
public interface InsightApiRequest {

    /**
     * 接口计数用法的 {@code usageType}
     */
    String QUANTITY_USAGE = "QUANTITY";

    /**
     * 创建一个新的 {@code InsightApiRequest} 构建器。
     *
     * @return {@code InsightApiRequest} 构建器实例
     */
    static Builder newBuilder() {
        return new InsightApiRequestImpl.InsightApiRequestBuilderImpl();
    }

    /**
     * {@code InsightApiRequest} 构建器接口。
     */
    interface Builder {

        /**
         * 设置 {@code usageType}。
         * <p>
         * 若不设置，则默认使用计数用法（{@code usageType} 值为 {@code "QUANTITY"}）。
         *
         * @param usageType 用法类型
         * @return 构建器实例
         */
        Builder usageType(String usageType);

        /**
         * 设置 {@code method}。
         * <p>
         * 不区分大小写。
         *
         * @param method 请求方法
         * @return 构建器实例
         */
        Builder method(String method);

        /**
         * 设置 {@code url}。
         * <p>
         * 请不要自行将路径变量或请求参数拼接到 {@code url} 中，
         * 使用 {@link Builder#pathVariable(Map)} 或 {@link Builder#requestParam(Map)} 方法进行设置。
         *
         * @param url URL
         * @return 构建器实例
         */
        Builder url(String url);

        /**
         * 设置路径变量映射 {@code pathVariableMap}。
         * <p>
         * 路径变量是键值对的形式，将会被替换到请求的 URL 中。
         *
         * @param requestParamMap 请求参数映射
         * @return 构建器实例
         */
        Builder pathVariable(Map<String, String> requestParamMap);

        /**
         * 设置请求参数映射 {@code requestParamMap}。
         * <p>
         * 请求参数是键值对的形式，将会被拼接到请求的 URL 中。
         *
         * @param requestParamMap 请求参数映射
         * @return 构建器实例
         */
        Builder requestParam(Map<String, String> requestParamMap);

        /**
         * 设置请求头映射 {@code requestHeaderMap}。
         * <p>
         * 请求头可以包含多个键值对，且一个键通常对应于一个值。
         * <p>
         * 如果请求有请求体，必须设置 Content-Type 请求头。
         *
         * @param requestHeaderMap 请求头映射
         * @return 构建器实例
         */
        Builder requestHeader(Map<String, String> requestHeaderMap);

        /**
         * 设置请求体 {@code requestBody}。
         * <p>
         * 请求体可以是任意类型的对象，用于传递请求的主体数据。
         * <p>
         * 请求体也可以是 {@link InputStream} 及其子类或 {@code byte[]} 类型。
         * <p>
         * 其他请求体对象将会被序列化为 JSON 字符串进行传输，也可以直接传入 JSON 字符串。
         * <p>
         *
         * @param requestBody 请求体
         * @return 构建器实例
         */
        Builder requestBody(Object requestBody);

        /**
         * 设置超时时间 {@code timeout}。
         * <p>
         * 用于设置请求的超时时间。
         * <p>
         * 如果不设置，请求将阻塞，直至获得响应。
         *
         * @param timeout 超时时间
         * @return 构建器实例
         */
        Builder timeout(Duration timeout);

        /**
         * 构建 {@link InsightApiRequest} 对象。
         *
         * @return {@code InsightApiRequest} 示例
         */
        InsightApiRequest build();

    }

    /**
     * 获取 {@code usageType}。
     * <p>
     * 如果在请求的构建器中未设置 {@code usageType}，默认使用计数用法，
     * （{@code usageType} 值为 {@code "QUANTITY"}）。
     *
     * @return 用法类型
     */
    String usageType();

    /**
     * 获取请求方法。
     *
     * <p>请求方法在请求对象创建时被指定，且不能为空或空白字符串。</p>
     *
     * @return 请求方法
     */
    String method();

    /**
     * 获取请求 URL。
     * <p>
     * 请求 URL 在请求对象创建时被指定，不能为空或空白字符串。
     * <p>
     * 返回原始的 URL，包含路径变量的占位符，但不包含请求参数，
     * <p>
     *
     * @return 请求 URL
     */
    String url();

    /**
     * 返回一个包含此请求的 {@code pathVariable} 的 {@code Optional} 对象。
     * <p>
     * 如果在请求的构建器中未设置 {@code pathVariable}，则 {@code Optional} 为空。
     *
     * @return 包含此请求的 {@code pathVariable} 的 {@code Optional} 对象
     */
    Optional<Map<String, String>> pathVariable();

    /**
     * 返回一个包含此请求的 {@code requestParam} 的 {@code Optional} 对象。
     * <p>
     * 如果在请求的构建器中未设置 {@code requestParam}，则 {@code Optional} 为空。
     *
     * @return 包含此请求的 {@code requestParam} 的 {@code Optional} 对象
     */
    Optional<Map<String, String>> requestParam();

    /**
     * 返回一个包含此请求的 {@code requestHeader} 的 {@code Optional} 对象。
     * <p>
     * 如果在请求的构建器中未设置 {@code requestHeader}，则 {@code Optional} 为空。
     *
     * @return 包含此请求的 {@code requestHeader} 的 {@code Optional} 对象
     */
    Optional<Map<String, String>> requestHeader();

    /**
     * 返回一个包含此请求的 {@code requestBody} 的 {@code Optional} 对象。
     * <p>
     * 如果在请求的构建器中未设置 {@code requestBody}，则 {@code Optional} 为空。
     *
     * @return 包含此请求的 {@code requestBody} 的 {@code Optional} 对象
     */
    Optional<Object> requestBody();

    /**
     * 返回一个包含此请求的 {@code timeout} 的 {@code Optional} 对象。
     * <p>
     * 如果在请求的构建器中未设置 {@code timeout}，则 {@code Optional} 为空。
     *
     * @return 包含此请求的 {@code timeout} 的 {@code Optional} 对象
     */
    Optional<Duration> timeout();

}
