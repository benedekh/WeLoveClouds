package weloveclouds.server.services.transaction.tasks.twopc;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;

import weloveclouds.commons.kvstore.models.messages.IKVTransactionMessage.StatusType;
import weloveclouds.server.services.transaction.SenderTransaction;
import weloveclouds.server.services.transaction.tasks.TransactionTask;

/**
 * Init task for a transaction. Simply sends the INIT message to every participant.
 * 
 * @author Benedek
 */
public class InitTask extends TransactionTask<InitTask.Builder> {

    private static final Logger LOGGER = Logger.getLogger(InitTask.class);

    protected InitTask(Builder builder) {
        super(builder);
    }

    @Override
    protected void executeTask() throws InterruptedException, ExecutionException {
        LOGGER.debug("Init phase for transactions started.");

        Set<Future<StatusType>> futures = sendInitRequests();
        if (containsIDRegenerationNeeded(futures)) {
            regenerateTransactionId();
            executeTask();
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
            try {
                if (response.get(getMaxWaitingTimeInMillis(),
                        TimeUnit.MILLISECONDS) == StatusType.RESPONSE_GENERATE_NEW_ID) {
                    return true;
                }
            } catch (TimeoutException ex) {
                LOGGER.error(ex);
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

    /**
     * Builder pattern for creating a {@link InitTask} instance.
     *
     * @author Benedek
     */
    public static class Builder extends TransactionTask.Builder<Builder> {

        public InitTask build() {
            return new InitTask(this);
        }
    }

}
