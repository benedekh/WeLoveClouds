package weloveclouds.server.services.transaction.tasks;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import weloveclouds.commons.kvstore.models.messages.IKVTransactionMessage.StatusType;
import weloveclouds.server.services.transaction.SenderTransaction;
import weloveclouds.server.services.transaction.model.TransactionWithResponseStatus;

public class CommitReadyTask extends AbstractTransactionTask {

    public CommitReadyTask(ITransactionTask successorForSuccess, ITransactionTask successorForFail,
            Set<SenderTransaction> transactions, ExecutorService executorService) {
        super(successorForSuccess, successorForFail, transactions, executorService);
    }

    @Override
    protected void executeTask() throws InterruptedException, ExecutionException {
        Set<Future<TransactionWithResponseStatus>> futures = sendCommitReadyRequests();
        if (notEveryoneIsCommitReady(futures)) {
            throw new ExecutionException(new Exception("Not every participant is ready for commit."));
        }
    }

    private Set<Future<TransactionWithResponseStatus>> sendCommitReadyRequests() {
        Set<Future<TransactionWithResponseStatus>> futures = new HashSet<>();
        for (SenderTransaction transaction : transactions) {
            futures.add(executorService.submit(transaction.new CommitReady()));
        }
        return futures;
    }

    private boolean notEveryoneIsCommitReady(Set<Future<TransactionWithResponseStatus>> responses)
            throws InterruptedException, ExecutionException {
        return !everyonesHasTheExpectedStatus(responses, StatusType.RESPONSE_COMMIT_READY);
    }

}
