package weloveclouds.server.services.transaction.tasks;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import weloveclouds.commons.kvstore.models.messages.IKVTransactionMessage.StatusType;
import weloveclouds.server.services.transaction.SenderTransaction;
import weloveclouds.server.services.transaction.model.TransactionWithResponseStatus;

public class CommitTask extends AbstractTransactionTask {

    public CommitTask(ITransactionTask successorForFail, Set<SenderTransaction> transactions,
            ExecutorService executorService) {
        super(successorForFail, transactions, executorService);
    }

    public CommitTask(ITransactionTask successorForSuccess, ITransactionTask successorForFail,
            Set<SenderTransaction> transactions, ExecutorService executorService) {
        super(successorForSuccess, successorForFail, transactions, executorService);
    }

    @Override
    protected void executeTask() throws InterruptedException, ExecutionException {
        Set<Future<TransactionWithResponseStatus>> futures = sendCommitRequests();
        if (notEveryoneIsCommitted(futures)) {
            throw new ExecutionException(new Exception("Not every participant is committed."));
        }
    }

    private Set<Future<TransactionWithResponseStatus>> sendCommitRequests() {
        Set<Future<TransactionWithResponseStatus>> futures = new HashSet<>();
        for (SenderTransaction transaction : transactions) {
            futures.add(executorService.submit(transaction.new Commit()));
        }
        return futures;
    }

    private boolean notEveryoneIsCommitted(Set<Future<TransactionWithResponseStatus>> responses)
            throws InterruptedException, ExecutionException {
        return !everyonesHasTheExpectedStatus(responses, StatusType.RESPONSE_COMMITTED);
    }

}
