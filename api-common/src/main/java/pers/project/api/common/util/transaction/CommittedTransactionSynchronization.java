package pers.project.api.common.util.transaction;

import org.springframework.transaction.support.TransactionSynchronization;

/**
 * 提交后事务同步
 * <p>
 * 在事务提交后，执行委托的同步自定义操作。
 * <p>
 * 该类继承了 {@link AbstractDelegatedTransactionSynchronization} 抽象类，并重写其中的
 * {@link TransactionSynchronization#afterCommit()} 方法。
 *
 * @author Luo Fei
 * @date 2023/04/13
 * @see TransactionUtils
 */
public final class CommittedTransactionSynchronization extends AbstractDelegatedTransactionSynchronization {

    /**
     * 构造一个 {@code CommittedTransactionSynchronization} 实例。
     *
     * @param delegate 同步操作委托
     * @throws IllegalArgumentException 如果 {@code delegate} 参数为 {@code null}
     */
    public CommittedTransactionSynchronization(TxSyncDelegate delegate) {
        super(delegate);
    }

    /**
     * 事务提交后调用的同步方法。
     *
     * @see TransactionSynchronization#afterCommit()
     */
    @Override
    public void afterCommit() {
        delegate.execute();
    }

}

