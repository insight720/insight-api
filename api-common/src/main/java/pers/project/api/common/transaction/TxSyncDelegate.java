package pers.project.api.common.transaction;

import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.util.Assert;
import pers.project.api.common.util.TransactionUtils;

/**
 * 事务同步委托（TransactionSynchronizationDelegate）
 * <p>
 * 该接口描述了在事务上下文中，可以执行的自定义同步操作。
 * <p>
 * 具体而言，在事务提交或回滚的前或后，该接口 {@link #execute()} 方法中定义的代码将被执行。
 * <p>
 * 该接口提供了一个默认实现的方法 {@link #andThen(TxSyncDelegate)}，
 * 该方法允许将当前接口实例与另一个接口实例进行组合，从而可将多个不同的同步行为组合到一个事务上下文中执行。
 * <p>
 * 此接口的名称进行了缩写，以避免过长的类型声明。
 *
 * @author Luo Fei
 * @date 2023/04/12
 * @see AbstractDelegatedTransactionSynchronization
 * @see TransactionUtils
 */
@FunctionalInterface
public interface TxSyncDelegate {

    /**
     * 执行
     * <p>
     * 在事务上下文中，可以执行的自定义同步操作。
     * <p>
     * <b>注意：该方法中抛出的异常有可能导致事务回滚，也有可能不被传递给调用者。</b>
     * <pre>
     * 更多细节请查看 {@link TransactionSynchronization} 的相关说明，此方法的功能由它支持。</b>
     * </pre>
     */
    void execute();

    /**
     * 返回一个新的 {@link TxSyncDelegate} 实例，该实例会先执行当前实例的
     * {@code execute()} 方法，然后再执行 {@code next} 参数指定的 {@code TxSyncDelegate} 实例的
     * {@code execute()} 方法。
     *
     * @param next 在当前实例执行后需要执行的 {@code TxSyncDelegate} 实例。
     * @return 一个新的 {@code TxSyncDelegate} 实例，包含当前实例及其他指定实例的全部操作。
     * @throws IllegalArgumentException 如果传入的 {@code TxSyncDelegate} 实例为 {@code null}，则会抛出该异常。
     */
    default TxSyncDelegate andThen(TxSyncDelegate next) {
        Assert.notNull(next, "The next must be not null");
        return () -> {
            execute();
            next.execute();
        };
    }

}
