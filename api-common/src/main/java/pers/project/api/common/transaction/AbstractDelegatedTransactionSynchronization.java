package pers.project.api.common.transaction;

import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.util.Assert;
import pers.project.api.common.util.TransactionUtils;

/**
 * 抽象的委托事务同步
 * <p>
 * 该抽象类实现了 {@link TransactionSynchronization} 接口，并将事务上下文的同步操作委托给一个
 * {@link TxSyncDelegate} 实例来处理。
 * <p>
 * {@code TxSyncDelegate} 实例必须在构造方法中传入该类的实例，在事务提交或回滚的前或后，
 * {@code delegate} 实例的 {@link TxSyncDelegate#execute()} 方法将会被调用执行。
 * <p>
 * 该类除了 {@code delegate} 属性和相应的构造方法外，没有实现任何
 * {@link TransactionSynchronization} 接口的方法，子类可以覆盖接口默认实现来进行具体的操作。
 * 当然，子类也可以完全忽略某些默认方法。
 *
 * @author Luo Fei
 * @date 2023/04/12
 * @see TransactionUtils
 */
public abstract class AbstractDelegatedTransactionSynchronization implements TransactionSynchronization {

    /**
     * 事务同步委托实例
     *
     * @see TxSyncDelegate
     */
    protected final TxSyncDelegate delegate;

    /**
     * 构造一个 {@code AbstractDelegatedTransactionSynchronization} 的实例。
     *
     * @param delegate 负责处理事务上下文同步操作的委托实例
     * @throws IllegalArgumentException 如果 {@code delegate} 参数为 {@code null}
     */
    protected AbstractDelegatedTransactionSynchronization(TxSyncDelegate delegate) {
        Assert.notNull(delegate, "The delegate must be not null");
        this.delegate = delegate;
    }

}
