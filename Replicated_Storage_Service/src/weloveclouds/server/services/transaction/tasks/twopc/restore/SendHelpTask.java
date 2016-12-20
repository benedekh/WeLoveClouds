package weloveclouds.server.services.transaction.tasks.twopc.restore;

import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;
import org.joda.time.Duration;

import weloveclouds.commons.kvstore.models.messages.IKVTransactionMessage.StatusType;
import weloveclouds.server.services.transaction.SenderTransaction;
import weloveclouds.server.services.transaction.tasks.TransactionTask;

public class SendHelpTask extends TransactionTask<SendHelpTask.Builder> {

    private static final Logger LOGGER = Logger.getLogger(SendHelpTask.class);
    private static final Duration MAX_WAITING_TIME_FOR_RESPONSE = new Duration(2 * 1000);

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
                response.get(MAX_WAITING_TIME_FOR_RESPONSE.getMillis(), TimeUnit.MILLISECONDS);
            } catch (TimeoutException ex) {
                throw new ExecutionException(ex);
            }
        }
    }

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
