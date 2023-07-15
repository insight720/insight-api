package pers.project.api.client;

import pers.project.api.client.exeception.InsightApiClientException;
import pers.project.api.client.impl.InsightClientImpl;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * Insight API 的客户端接口
 *
 * @author Luo Fei
 * @date 2023/07/11
 */
public interface InsightApiClient {

    /**
     * 创建一个新的 {@code InsightApiClient} 构建器。
     *
     * @return {@code InsightApiClient} 构建器实例
     */
    static Builder newBuilder() {
        return new InsightClientImpl.InsightApiClientBuilderImpl();
    }

    /**
     * 同步发送 {@link InsightApiRequest} 并返回 {@link InsightApiResponse}。
     *
     * @param insightApiRequest {@code  InsightApiRequest} 请求
     * @param responseBodyClass 响应体类型
     * @return InsightApiResponse 响应
     * @throws InsightApiClientException 如果发送请求时发生异常
     *
     */
    <T> InsightApiResponse<T> send(InsightApiRequest insightApiRequest, Class<T> responseBodyClass);

    /**
     * 异步发送 {@link InsightApiRequest} 并返回 {@link CompletableFuture}。
     *
     * @param insightApiRequest           {@code InsightApiRequest} 请求
     * @param responseBodyClass 响应体类型
     * @return {@code CompletableFuture} 异步响应
     * @throws InsightApiClientException 如果发送请求时发生异常
     */
    <T> CompletableFuture<InsightApiResponse<T>> sendAsync(InsightApiRequest insightApiRequest, Class<T> responseBodyClass);

    /**
     * {@code InsightApiClient} 构建器接口。
     */
    interface Builder {

        /**
         * 设置 secretId。
         *
         * @param secretId 密钥 ID
         * @return 构建器实例
         */
        Builder secretId(String secretId);

        /**
         * 设置 secretKey。
         *
         * @param secretKey 密钥值
         * @return 构建器实例
         */
        Builder secretKey(String secretKey);

        /**
         * 设置 {@link Executor}。
         *
         * @param executor 执行线程池
         * @return 构建器实例
         * @see HttpClient.Builder#executor(Executor)
         */
        Builder executor(Executor executor);

        /**
         * 设置连接超时时间。
         *
         * @param connectTimeout 超时时间
         * @return 构建器实例
         * @see HttpClient.Builder#connectTimeout(Duration)
         */
        Builder connectTimeout(Duration connectTimeout);

        /**
         * 构建 {@link InsightApiClient} 实例。
         *
         * @return {@code InsightApiClient} 实例
         */
        InsightApiClient build();

    }

    /**
     * 获取密钥 ID。
     *
     * <p>此方法用于获取客户端的密钥 ID，该 ID 在客户端创建时被指定。密钥 ID 不能为空或空白字符串。</p>
     *
     * @return 密钥ID
     */
    String secretId();

    /**
     * 获取密钥值。
     *
     * <p>此方法用于获取客户端的密钥值，该值在客户端创建时被指定。密钥值不能为空或空白字符串。</p>
     *
     * @return 密钥值
     */
    String secretKey();

    /**
     * 返回一个包含此客户端的 {@link Executor} 的 {@code Optional} 对象。
     * 如果在客户端的构建器中未设置 {@code Executor}，则 {@code Optional} 为空。
     *
     * <p>尽管此方法可能返回一个空的 {@code Optional}，但是 {@code HttpClient} 仍然可以有一个未公开的 {@linkplain
     * InsightApiClient.Builder#executor(Executor) 默认执行器} 用于执行异步和依赖任务。
     *
     * @return 包含此客户端的 {@code Executor} 的 {@code Optional} 对象
     */
    Optional<Executor> executor();

    /**
     * 返回一个 {@code Optional} 对象，其中包含此客户端的连接超时时长。
     * <p>
     * 如果在客户端的构建器中未设置 {@link Builder#connectTimeout(Duration)}，
     * 则 {@code Optional} 为空。
     *
     * @return 包含此客户端连接超时时长的 {@code Optional} 对象
     */
    Optional<Duration> connectTimeout();

}
