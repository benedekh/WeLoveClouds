package weloveclouds.server.services.transaction.flow.twopc;

import java.util.Set;

import weloveclouds.server.services.transaction.SenderTransaction;
import weloveclouds.server.services.transaction.flow.TransactionExecutionFlow;
import weloveclouds.server.services.transaction.tasks.ITransactionTask;
import weloveclouds.server.services.transaction.tasks.twopc.AbortTask;
import weloveclouds.server.services.transaction.tasks.twopc.CommitReadyTask;
import weloveclouds.server.services.transaction.tasks.twopc.CommitTask;
import weloveclouds.server.services.transaction.tasks.twopc.InitTask;

/**
 * Steps in a two-phase-commit protocol to be executed.
 * 
 * @author Benedek
 */
public class TwoPCCoordinatorExecutionFlow extends TransactionExecutionFlow {

    @Override
    public void executeFlowForTransactions(Set<SenderTransaction> transactions) throws Exception {
        ITransactionTask abortTask = new AbortTask.Builder().transactions(transactions)
                .executorService(executorService).build();
        ITransactionTask commitTask = new CommitTask.Builder().successorForFail(abortTask)
                .transactions(transactions).executorService(executorService).build();
        ITransactionTask commitReadyTask = new CommitReadyTask.Builder()
                .successorForSuccess(commitTask).successorForFail(abortTask)
                .transactions(transactions).executorService(executorService).build();
        ITransactionTask initTask = new InitTask.Builder().successorForSuccess(commitReadyTask)
                .successorForFail(abortTask).transactions(transactions)
                .executorService(executorService).build();
        initTask.execute();
    }

}
