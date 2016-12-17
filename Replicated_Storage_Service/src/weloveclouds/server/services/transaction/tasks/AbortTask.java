package weloveclouds.server.services.transaction.tasks;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import weloveclouds.commons.kvstore.models.messages.IKVTransactionMessage.StatusType;
import weloveclouds.server.services.transaction.SenderTransaction;
import weloveclouds.server.services.transaction.model.TransactionWithResponseStatus;

public class AbortTask extends AbstractTransactionTask {

    public AbortTask(Set<SenderTransaction> transactions, ExecutorService executorService) {
        super(transactions, executorService);
    }

    @Override
    protected void executeTask() throws InterruptedException, ExecutionException {
        Set<Future<TransactionWithResponseStatus>> futures = sendAbortRequests();
        if (notEveryoneIsAborted(futures)) {
            throw new ExecutionException(new Exception("Not every participant is aborted."));
        }
    }

    private Set<Future<TransactionWithResponseStatus>> sendAbortRequests() {
        Set<Future<TransactionWithResponseStatus>> futures = new HashSet<>();
        for (SenderTransaction transaction : transactions) {
            futures.add(executorService.submit(transaction.new Abort()));
        }
        return futures;
    }

    private boolean notEveryoneIsAborted(Set<Future<TransactionWithResponseStatus>> responses)
            throws InterruptedException, ExecutionException {
        return !everyonesHasTheExpectedStatus(responses, StatusType.RESPONSE_ABORTED);
    }

}
