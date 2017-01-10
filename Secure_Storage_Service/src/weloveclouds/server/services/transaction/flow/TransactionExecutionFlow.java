package weloveclouds.server.services.transaction.flow;

import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import weloveclouds.server.services.transaction.SenderTransaction;

/**
 * An abstract transaction execution flow, which defines the steps to be executed for each
 * transaction.
 * 
 * @author Benedek
 */
public abstract class TransactionExecutionFlow implements ITransactionExecutionFlow {

    private static final Logger LOGGER = Logger.getLogger(TransactionExecutionFlow.class);

    protected ExecutorService executorService;

    @Override
    public void executeTransactions(Set<SenderTransaction> transactions) {
        try {
            if (transactions.size() > 0) {
                executorService = Executors.newFixedThreadPool(transactions.size());
                try {
                    executeFlowForTransactions(transactions);
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

    protected abstract void executeFlowForTransactions(Set<SenderTransaction> transactions)
            throws Exception;
}
