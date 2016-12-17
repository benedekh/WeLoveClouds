package weloveclouds.server.services.transaction.tasks;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import weloveclouds.commons.kvstore.models.messages.IKVTransactionMessage.StatusType;
import weloveclouds.server.services.transaction.SenderTransaction;
import weloveclouds.server.services.transaction.model.TransactionWithResponseStatus;

public class InitTask extends AbstractTransactionTask {

    private boolean isAlreadyExecuted;

    public InitTask(ITransactionTask successorForSuccess, ITransactionTask successorForFail,
            Set<SenderTransaction> transactions, ExecutorService executorService) {
        super(successorForSuccess, successorForFail, transactions, executorService);
    }

    public InitTask(ITransactionTask successorForSuccess, ITransactionTask successorForFail,
            Set<SenderTransaction> transactions, ExecutorService executorService,
            boolean isAlreadyExecuted) {
        super(successorForSuccess, successorForFail, transactions, executorService);
        this.isAlreadyExecuted = isAlreadyExecuted;
    }

    @Override
    protected void executeTask() throws InterruptedException, ExecutionException {
        Set<Future<TransactionWithResponseStatus>> futures = sendInitRequests();

        if (containsIDRegenerationNeeded(futures)) {
            regenerateTransactionId();
            if (!isAlreadyExecuted) {
                executeTask();
            } else {
                throw new ExecutionException(
                        new Exception("Transaction initialization step was already executed."));
            }
        }
        if (notEveryoneIsInitReady(futures)) {
            throw new ExecutionException(
                    new Exception("Not every participant is ready for a new transaction."));
        }
    }

    private Set<Future<TransactionWithResponseStatus>> sendInitRequests() {
        Set<Future<TransactionWithResponseStatus>> futures = new HashSet<>();
        for (SenderTransaction transaction : transactions) {
            futures.add(executorService.submit(transaction.new Init()));
        }
        return futures;
    }

    private boolean containsIDRegenerationNeeded(
            Set<Future<TransactionWithResponseStatus>> responses)
            throws InterruptedException, ExecutionException {
        for (Future<TransactionWithResponseStatus> response : responses) {
            if (response.get().getResponseStatus() == StatusType.RESPONSE_GENERATE_NEW_ID) {
                return true;
            }
        }
        return false;
    }

    private boolean notEveryoneIsInitReady(Set<Future<TransactionWithResponseStatus>> responses)
            throws InterruptedException, ExecutionException {
        return !everyonesHasTheExpectedStatus(responses, StatusType.RESPONSE_INIT_READY);
    }


    private void regenerateTransactionId() {
        UUID transactionId = UUID.randomUUID();
        for (SenderTransaction transaction : transactions) {
            transaction.setTransactionId(transactionId);
        }
    }

}
