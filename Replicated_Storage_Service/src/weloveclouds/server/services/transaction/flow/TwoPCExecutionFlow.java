package weloveclouds.server.services.transaction.flow;

import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import weloveclouds.server.services.transaction.SenderTransaction;
import weloveclouds.server.services.transaction.tasks.ITransactionTask;
import weloveclouds.server.services.transaction.tasks.twopc.AbortTask;
import weloveclouds.server.services.transaction.tasks.twopc.CommitReadyTask;
import weloveclouds.server.services.transaction.tasks.twopc.CommitTask;
import weloveclouds.server.services.transaction.tasks.twopc.InitTask;

public class TwoPCExecutionFlow implements ITransactionExecutionFlow {

    private static final Logger LOGGER = Logger.getLogger(TwoPCExecutionFlow.class);

    @Override
    public void executeTransactions(Set<SenderTransaction> transactions) {
        try {
            if (transactions.size() > 0) {
                ExecutorService executorService = Executors.newFixedThreadPool(transactions.size());
                try {
                    ITransactionTask abortTask = new AbortTask.Builder().transactions(transactions)
                            .executorService(executorService).build();
                    ITransactionTask commitTask = new CommitTask.Builder()
                            .successorForFail(abortTask).transactions(transactions)
                            .executorService(executorService).build();
                    ITransactionTask commitReadyTask = new CommitReadyTask.Builder()
                            .successorForSuccess(commitTask).successorForFail(abortTask)
                            .transactions(transactions).executorService(executorService).build();
                    ITransactionTask initTask = new InitTask.Builder()
                            .successorForSuccess(commitReadyTask).successorForFail(abortTask)
                            .transactions(transactions).executorService(executorService).build();
                    initTask.execute();
                } finally {
                    LOGGER.debug("Shutting down transactions and executor service.");
                    closeTransactions(transactions);
                    executorService.shutdown();
                }
            }
        } catch (Exception ex) {
            LOGGER.error(ex);
        }
    }

    private void closeTransactions(Set<SenderTransaction> transactions) {
        for (SenderTransaction transaction : transactions) {
            transaction.close();
        }
    }

}
