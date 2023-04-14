package pers.project.api.common.util.transaction;

import org.springframework.transaction.support.TransactionSynchronization;

/**
 * 提交前事务同步
 * <p>
 * 在事务提交前，如果事务只读状态与预期相符，则执行委托的同步自定义操作。
 * <p>
 * 该类继承了 {@link AbstractDelegatedTransactionSynchronization} 抽象类，并重写其中的
 * {@link TransactionSynchronization#beforeCommit(boolean)} 方法。
 * <p>
 * 该类中包含两个 {@code static} 工厂方法，分别用于创建读写（{@code read-write}）事务的同步实例
 * 和只读（{@code read-only}）事务的同步实例。
 *
 * @author Luo Fei
 * @date 2023/04/13
 * @see TransactionUtils
 */
public final class CommittingTransactionSynchronization extends AbstractDelegatedTransactionSynchronization {

    /**
     * 实例是否用于只读事务
     * <p>
     * 如果在只读事务中使用，则设置为 {@code true}。
     */
    private final boolean readOnly;

    /**
     * 创建一个仅用于读写事务的提交前事务同步实例。
     *
     * @param delegate 同步操作委托
     * @return 读写事务的提交前事务同步实例
     */
    public static CommittingTransactionSynchronization readWrite(TxSyncDelegate delegate) {
        return new CommittingTransactionSynchronization(delegate, false);
    }

    /**
     * 创建一个仅用于只读事物的提交前事务同步实例。
     *
     * @param delegate 同步操作委托
     * @return 只读事物的提交前事务同步实例
     */
    public static CommittingTransactionSynchronization readOnly(TxSyncDelegate delegate) {
        return new CommittingTransactionSynchronization(delegate, true);
    }

    /**
     * 事务完成前调用的同步方法。
     *
     * @param readOnly 当前事务是否只读
     * @throws IllegalCallerException 如果事务的实际只读状态与预期不符
     * @see TransactionSynchronization#beforeCommit(boolean)
     */
    @Override
    public void beforeCommit(boolean readOnly) {
        // 事务的实际只读状态与预期相符，则执行委托实例的同步方法
        if (this.readOnly == readOnly) {
            delegate.execute();
            return;
        }
        // 事务的实际只读状态与预期不符，则抛出异常
        String correctUsage = (readOnly ? "read-only" : "read-write");
        String wrongUsage = (readOnly ? "read-write" : "read-only");
        throw new IllegalCallerException("""
                Transaction is %s, but %s synchronization is used
                """.formatted(correctUsage, wrongUsage));
    }

    /**
     * 构造一个 {@code CommittingTransactionSynchronization} 实例。
     * <p>
     * 该构造函数是私有的，因为使用 {@code static} 工厂方法创建实例有更清晰的语义。
     *
     * @param delegate 同步操作委托
     * @param readOnly 实例是否用于只读事务
     * @throws IllegalArgumentException 如果 {@code delegate} 参数为 {@code null}
     */
    private CommittingTransactionSynchronization(TxSyncDelegate delegate, boolean readOnly) {
        super(delegate);
        this.readOnly = readOnly;
    }

}

