package pers.project.api.common.util;

import org.springframework.transaction.NoTransactionException;
import org.springframework.transaction.TransactionExecution;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import pers.project.api.common.transaction.*;

import static org.springframework.transaction.support.TransactionSynchronizationManager.isActualTransactionActive;

/**
 * 事务工具类
 * <p>
 * 提供在 Spring 事务上下文中的一些简便方法。
 *
 * @author Luo Fei
 * @date 2023/04/12
 * @see TxSyncDelegate
 * @see AbstractDelegatedTransactionSynchronization
 */
public abstract class TransactionUtils {

    /**
     * 完成时回滚事务
     * <p>
     * 在 {@link Transactional} 支持的声明式事务上下文中调用该方法后，
     * 后续处于事务中的代码依然会执行，但执行后事务唯一的结果将是回滚。
     *
     * @throws NoTransactionException 如果当前不在事务上下文中
     * @see TransactionExecution#setRollbackOnly()
     */
    public static void rollBackOnCompletion() {
        throwExceptionIfTransactionInactive();
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
    }

    /**
     * 在只读事务提交之前执行同步操作
     * <p>
     * 仅用于只读事务，在可读写事务中使用将抛出异常。
     * <p>
     * 此方法功能由 {@link TransactionSynchronization#beforeCommit(boolean)} 支持，
     * 使用前请阅读其说明。
     *
     * @throws NoTransactionException 如果当前不在事务上下文中
     * @throws IllegalCallerException 如果在可读写事务中使用
     * @see TxSyncDelegate
     * @see CommittingTransactionSynchronization
     */
    public static void beforeReadOnlyCommit(TxSyncDelegate delegate) {
        throwExceptionIfTransactionInactive();
        TransactionSynchronizationManager.registerSynchronization(CommittingTransactionSynchronization.readOnly(delegate));
    }

    /**
     * 在可读写事务提交之前执行同步操作
     * <p>
     * 仅用于可读写事务，在只读事务中使用将抛出异常。
     * <p>
     * 此方法功能由 {@link TransactionSynchronization#beforeCommit(boolean)} 支持，
     * 使用前请阅读其说明。
     *
     * @throws NoTransactionException 如果当前不在事务上下文中
     * @throws IllegalCallerException 如果在只读事务中使用
     * @see TxSyncDelegate
     * @see CommittingTransactionSynchronization
     */
    public static void beforeReadWriteCommit(TxSyncDelegate delegate) {
        throwExceptionIfTransactionInactive();
        TransactionSynchronizationManager.registerSynchronization(CommittingTransactionSynchronization.readWrite(delegate));
    }

    /**
     * 在事务完成之前执行同步操作
     * <p>
     * 此方法功能由 {@link TransactionSynchronization#beforeCompletion()} 支持，
     * 使用前请阅读其说明。
     *
     * @throws NoTransactionException 如果当前不在事务上下文中
     * @see TxSyncDelegate
     * @see CompletingTransactionSynchronization
     */
    public static void beforeCompletion(TxSyncDelegate delegate) {
        throwExceptionIfTransactionInactive();
        TransactionSynchronizationManager.registerSynchronization(new CompletingTransactionSynchronization(delegate));
    }

    /**
     * 在事务提交之后执行同步操作
     * <p>
     * 此方法功能由 {@link TransactionSynchronization#afterCommit()} 支持，
     * 使用前请阅读其说明。
     *
     * @throws NoTransactionException 如果当前不在事务上下文中
     * @see TxSyncDelegate
     * @see CommittedTransactionSynchronization
     */
    public static void afterCommit(TxSyncDelegate delegate) {
        throwExceptionIfTransactionInactive();
        TransactionSynchronizationManager.registerSynchronization(new CommittedTransactionSynchronization(delegate));
    }

    /**
     * 在事务完成之后执行同步操作
     * <p>
     * 此方法功能由 {@link TransactionSynchronization#afterCompletion(int)} 支持，
     * 使用前请阅读其说明。
     *
     * @throws NoTransactionException 如果当前不在事务上下文中
     * @see TxSyncDelegate
     * @see CompletedTransactionSynchronization
     */
    public static void afterCompletion(TxSyncDelegate delegate) {
        throwExceptionIfTransactionInactive();
        TransactionSynchronizationManager.registerSynchronization(CompletedTransactionSynchronization.committed(delegate));
    }

    /**
     * 如果事务回滚，则执行同步操作。
     * <p>
     * 此方法功能由 {@link TransactionSynchronization#afterCompletion(int)} 支持，
     * 使用前请阅读其说明。
     *
     * @throws NoTransactionException 如果当前不在事务上下文中
     * @see TxSyncDelegate
     * @see CompletedTransactionSynchronization
     */
    public static void ifRolledBackAfterCompletion(TxSyncDelegate delegate) {
        throwExceptionIfTransactionInactive();
        TransactionSynchronizationManager.registerSynchronization(CompletedTransactionSynchronization.rolledBack(delegate));
    }

    /**
     * 如果事务提交，则执行同步操作。
     * <p>
     * 此方法功能由 {@link TransactionSynchronization#afterCompletion(int)} 支持，
     * 使用前请阅读其说明。
     *
     * @throws NoTransactionException 如果当前不在事务上下文中
     * @see TxSyncDelegate
     * @see CompletedTransactionSynchronization
     */
    public static void ifCommittedAfterCompletion(TxSyncDelegate delegate) {
        throwExceptionIfTransactionInactive();
        TransactionSynchronizationManager.registerSynchronization(CompletedTransactionSynchronization.committed(delegate));
    }

    /**
     * 如果事务以未知状态完成，则执行同步操作。
     * <p>
     * 此方法功能由 {@link TransactionSynchronization#afterCompletion(int)} 支持，
     * 使用前请阅读其说明。
     *
     * @throws NoTransactionException 如果当前不在事务上下文中
     * @see TxSyncDelegate
     * @see CompletedTransactionSynchronization
     */
    public static void ifUnknownAfterCompletion(TxSyncDelegate delegate) {
        throwExceptionIfTransactionInactive();
        TransactionSynchronizationManager.registerSynchronization(CompletedTransactionSynchronization.unknown(delegate));
    }

    /**
     * 注册原生的 {@link TransactionSynchronization} 。
     * <p>
     * 当需要在事务上下文执行特定的同步操作时，可以使用两种不同的方式：
     * <p>
     * 1. 使用 {@link TxSyncDelegate} 接口
     * <p>
     * 2. 直接使用原生的 {@link TransactionSynchronization} 接口。
     * <p>
     * 使用原生的 {@link TransactionSynchronization} 接口相比使用 {@link TxSyncDelegate} 拥有更高的效率，
     * 因为它依赖于更少的中间对象。然而，使用原生接口可能会降低代码的可读性并增加编码难度。
     * <p>
     * 此方法功能由 {@link TransactionSynchronization} 支持，使用前请阅读其说明。
     *
     * @throws NoTransactionException 如果当前不在事务上下文中
     */
    public static void registerSynchronization(TransactionSynchronization synchronization) {
        throwExceptionIfTransactionInactive();
        TransactionSynchronizationManager.registerSynchronization(synchronization);
    }

    /**
     * 如果当前不在事务上下文中，则抛出异常。
     * <p>
     * 主要用于此工具类，也可用于其他需要确保事务生效的程序。
     *
     * @throws NoTransactionException 无事务异常
     */
    public static void throwExceptionIfTransactionInactive() {
        if (isActualTransactionActive()) {
            return;
        }
        throw new NoTransactionException("Transaction is not active");
    }

}
