package weloveclouds.server.services.transaction.flow;

import java.util.Set;

import weloveclouds.server.services.transaction.SenderTransaction;

public interface ITransactionExecutionFlow {

    public void executeTransactions(Set<SenderTransaction> transactions);

}
