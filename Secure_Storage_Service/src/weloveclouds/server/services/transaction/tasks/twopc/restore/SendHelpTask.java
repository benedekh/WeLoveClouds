package weloveclouds.server.services.transaction.tasks.twopc.restore;

import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;

import weloveclouds.commons.kvstore.models.messages.IKVTransactionMessage.StatusType;
import weloveclouds.server.services.transaction.SenderTransaction;
import weloveclouds.server.services.transaction.tasks.TransactionTask;

/**
 * Help task for a transaction. Simply sends the HELP message to every participant.
 * 
 * @author Benedek
 */
public class SendHelpTask extends TransactionTask<SendHelpTask.Builder> {

    private static final Logger LOGGER = Logger.getLogger(SendHelpTask.class);

    private Set<Future<StatusType>> setForResponses;

    protected SendHelpTask(Builder builder) {
        super(builder);
        this.setForResponses = builder.setForResponses;
    }

    @Override
    protected void executeTask() throws InterruptedException, ExecutionException {
        LOGGER.debug("Send help phase for transaction started.");
        sendHelpRequests();
        checkIfEveryoneResponded();
        LOGGER.debug("Send help phase for transaction finished.");
    }

    private void sendHelpRequests() {
        for (SenderTransaction transaction : transactions) {
            setForResponses.add(executorService.submit(transaction.new Help()));
        }
    }

    private void checkIfEveryoneResponded() throws InterruptedException, ExecutionException {
        for (Future<StatusType> response : setForResponses) {
            try {
                response.get(getMaxWaitingTimeInMillis(), TimeUnit.MILLISECONDS);
            } catch (TimeoutException ex) {
                LOGGER.error(ex);
            }
        }
    }

    /**
     * Builder pattern for creating a {@link SendHelpTask} instance.
     *
     * @author Benedek
     */
    public static class Builder extends TransactionTask.Builder<SendHelpTask.Builder> {
        private Set<Future<StatusType>> setForResponses;

        public Builder setForResponses(Set<Future<StatusType>> setForResponses) {
            this.setForResponses = setForResponses;
            return this;
        }

        public SendHelpTask build() {
            return new SendHelpTask(this);
        }
    }
}
