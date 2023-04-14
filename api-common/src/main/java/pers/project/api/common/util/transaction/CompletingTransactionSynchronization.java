package pers.project.api.common.util.transaction;

import org.springframework.transaction.support.TransactionSynchronization;

/**
 * 完成前事务同步
 * <p>
 * 在事务完成前，执行委托的同步自定义操作。
 * <p>
 * 该类继承了 {@link AbstractDelegatedTransactionSynchronization} 抽象类，并重写其中的
 * {@link TransactionSynchronization#beforeCompletion()} 方法。
 *
 * @author Luo Fei
 * @date 2023/04/13
 * @see TransactionUtils
 */
public class CompletingTransactionSynchronization extends AbstractDelegatedTransactionSynchronization {

    /**
     * 构造一个 {@code CompletingTransactionSynchronization} 实例。
     *
     * @param delegate 同步操作委托
     * @throws IllegalArgumentException 如果 {@code delegate} 参数为 {@code null}
     */
    public CompletingTransactionSynchronization(TxSyncDelegate delegate) {
        super(delegate);
    }

    /**
     * 事务完成前调用的同步方法。
     *
     * @see TransactionSynchronization#beforeCompletion()
     */
    @Override
    public void beforeCompletion() {
        delegate.execute();
    }

}
