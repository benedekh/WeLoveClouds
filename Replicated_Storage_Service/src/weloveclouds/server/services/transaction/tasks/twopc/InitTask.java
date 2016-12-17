package weloveclouds.server.services.transaction.tasks.twopc;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;

import weloveclouds.commons.kvstore.models.messages.IKVTransactionMessage.StatusType;
import weloveclouds.server.services.transaction.SenderTransaction;
import weloveclouds.server.services.transaction.tasks.TransactionCoordinatorTask;

public class InitTask extends TransactionCoordinatorTask<InitTask.Builder> {

    private static final Logger LOGGER = Logger.getLogger(InitTask.class);

    private boolean isAlreadyExecuted;

    protected InitTask(Builder builder) {
        super(builder);
    }

    @Override
    protected void executeTask() throws InterruptedException, ExecutionException {
        LOGGER.debug("Init phase for transactions started.");

        Set<Future<StatusType>> futures = sendInitRequests();
        if (containsIDRegenerationNeeded(futures)) {
            regenerateTransactionId();
            if (!isAlreadyExecuted) {
                isAlreadyExecuted = true;
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

        LOGGER.debug("Init phase for transactions finished.");
    }

    private Set<Future<StatusType>> sendInitRequests() {
        Set<Future<StatusType>> futures = new HashSet<>();
        for (SenderTransaction transaction : transactions) {
            futures.add(executorService.submit(transaction.new Init()));
        }
        return futures;
    }

    private boolean containsIDRegenerationNeeded(Set<Future<StatusType>> responses)
            throws InterruptedException, ExecutionException {
        for (Future<StatusType> response : responses) {
            if (response.get() == StatusType.RESPONSE_GENERATE_NEW_ID) {
                return true;
            }
        }
        return false;
    }

    private boolean notEveryoneIsInitReady(Set<Future<StatusType>> responses)
            throws InterruptedException, ExecutionException {
        return !everyonesHasTheExpectedStatus(responses, StatusType.RESPONSE_INIT_READY);
    }


    private void regenerateTransactionId() {
        LOGGER.debug("Regenerating transaction ID, because one of the participants need it.");
        UUID transactionId = UUID.randomUUID();
        for (SenderTransaction transaction : transactions) {
            transaction.setTransactionId(transactionId);
        }
    }

    public static class Builder extends TransactionCoordinatorTask.Builder<Builder> {

        @Override
        protected Builder getThis() {
            return this;
        }

        @Override
        public TransactionCoordinatorTask<Builder> build() {
            return new InitTask(this);
        }
    }

}
