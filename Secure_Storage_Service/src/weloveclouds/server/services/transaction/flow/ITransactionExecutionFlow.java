package weloveclouds.server.services.transaction.flow;

import java.util.Set;

import weloveclouds.server.services.transaction.SenderTransaction;

/**
 * Represents different steps which have to be performed for each transaction instance.
 * 
 * @author Benedek
 */
public interface ITransactionExecutionFlow {

    /**
     * Executes the respective transactions.
     */
    public void executeTransactions(Set<SenderTransaction> transactions);

}
