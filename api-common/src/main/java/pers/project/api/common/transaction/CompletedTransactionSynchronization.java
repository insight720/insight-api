package pers.project.api.common.transaction;

import org.springframework.transaction.support.TransactionSynchronization;
import pers.project.api.common.util.TransactionUtils;

/**
 * 完成后事务同步
 * <p>
 * 在事务完成后，如果状态符合当前实例的要求，则执行委托的同步自定义操作。
 * <p>
 * 该类继承了 {@link AbstractDelegatedTransactionSynchronization} 抽象类，并重写其中的
 * {@link TransactionSynchronization#afterCompletion(int)} 方法。
 * <p>
 * 借助该类中提供的几个 {@code static} 工厂方法，可以方便地创建四种不同状态要求的实例：
 * {@linkplain #committed(TxSyncDelegate) committed}（已提交）、{@linkplain #rolledBack(TxSyncDelegate) rolledBack}（已回滚）
 * 、{@linkplain #completed(TxSyncDelegate) unknown}（未知）或 {@linkplain #completed(TxSyncDelegate) completed}（完成）。
 *
 * @author Luo Fei
 * @date 2023/04/12
 * @see TransactionUtils
 */
public final class CompletedTransactionSynchronization extends AbstractDelegatedTransactionSynchronization {

    /**
     * 完成状态
     * <p>
     * 包括 {@code STATUS_COMMITTED}、{@code STATUS_ROLLED_BACK}、{@code STATUS_UNKNOWN} 三种状态。
     * <p>
     * 此状态是该类自定义的，用于指示该类实例在任何事务完成状态时均执行同步自定义操作。
     *
     * @see TransactionSynchronization#STATUS_COMMITTED
     * @see TransactionSynchronization#STATUS_ROLLED_BACK
     * @see TransactionSynchronization#STATUS_UNKNOWN
     */
    private static final int STATUS_COMPLETED = 3;

    /**
     * 执行同步自定义操作所要求的事务完成状态。
     *
     * @see TransactionSynchronization#STATUS_COMMITTED
     * @see TransactionSynchronization#STATUS_ROLLED_BACK
     * @see TransactionSynchronization#STATUS_UNKNOWN
     * @see CompletedTransactionSynchronization#STATUS_COMPLETED
     */
    private final int status;

    /**
     * 创建一个状态要求为 “已提交” 的完成后事务同步实例。
     *
     * @param delegate 委托的 {@link TxSyncDelegate} 实例
     * @return 状态为 {@code STATUS_COMMITTED} 的完成后事务同步实例
     */
    public static CompletedTransactionSynchronization committed(TxSyncDelegate delegate) {
        return new CompletedTransactionSynchronization(delegate, STATUS_COMMITTED);
    }

    /**
     * 创建一个状态要求为 “已回滚” 的完成后事务同步实例。
     *
     * @param delegate 委托的 {@link TxSyncDelegate} 实例
     * @return 状态为 {@code STATUS_ROLLED_BACK} 的完成后事务同步实例
     */
    public static CompletedTransactionSynchronization rolledBack(TxSyncDelegate delegate) {
        return new CompletedTransactionSynchronization(delegate, STATUS_ROLLED_BACK);
    }

    /**
     * 创建一个状态要求为 “未知” 的完成后事务同步实例。
     *
     * @param delegate 委托的 {@link TxSyncDelegate} 实例
     * @return 状态为 {@code STATUS_UNKNOWN} 的完成后事务同步实例
     */
    public static CompletedTransactionSynchronization unknown(TxSyncDelegate delegate) {
        return new CompletedTransactionSynchronization(delegate, STATUS_UNKNOWN);
    }

    /**
     * 创建一个状态要求为 “已完成” 的完成后事务同步实例。
     *
     * @param handler 委托的 {@link TxSyncDelegate} 实例
     * @return 状态为 {@code STATUS_COMPLETED} 的完成后事务同步实例
     */
    public static CompletedTransactionSynchronization completed(TxSyncDelegate handler) {
        return new CompletedTransactionSynchronization(handler, STATUS_COMPLETED);
    }

    /**
     * 事务完成后调用的同步方法。
     *
     * @see TransactionSynchronization#afterCompletion(int)
     */
    @Override
    public void afterCompletion(int status) {
        // 事务状态与当前实例要求的状态相同时，执行委托实例的同步方法
        if (status == this.status
            || this.status == STATUS_COMPLETED) {
            delegate.execute();
        }
    }

    /**
     * 构造一个 {@code CompletedTransactionSynchronization} 实例。
     * <p>
     * 该构造函数是私有的，防止传入错误的 {@code status} 参数。
     *
     * @param delegate       负责处理事务上下文同步操作的委托实例
     * @param status 实例要求的事务完成状态
     * @throws IllegalArgumentException 如果 {@code delegate} 参数为 {@code null}
     */
    private CompletedTransactionSynchronization(TxSyncDelegate delegate, int status) {
        super(delegate);
        this.status = status;
    }

}
