package weloveclouds.server.services.transaction.tasks.twopc;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;

import weloveclouds.commons.kvstore.models.messages.IKVTransactionMessage.StatusType;
import weloveclouds.server.services.transaction.SenderTransaction;
import weloveclouds.server.services.transaction.tasks.TransactionCoordinatorTask;

public class AbortTask extends TransactionCoordinatorTask<AbortTask.Builder> {

    private static final Logger LOGGER = Logger.getLogger(AbortTask.class);

    protected AbortTask(Builder builder) {
        super(builder);
    }

    @Override
    protected void executeTask() throws InterruptedException, ExecutionException {
        LOGGER.debug("Abort phase for transactions started.");
        Set<Future<StatusType>> futures = sendAbortRequests();
        if (notEveryoneIsAborted(futures)) {
            throw new ExecutionException(new Exception("Not every participant is aborted."));
        }
        LOGGER.debug("Abort phase for transactions finished.");
    }

    private Set<Future<StatusType>> sendAbortRequests() {
        Set<Future<StatusType>> futures = new HashSet<>();
        for (SenderTransaction transaction : transactions) {
            futures.add(executorService.submit(transaction.new Abort()));
        }
        return futures;
    }

    private boolean notEveryoneIsAborted(Set<Future<StatusType>> responses)
            throws InterruptedException, ExecutionException {
        return !everyonesHasTheExpectedStatus(responses, StatusType.RESPONSE_ABORTED);
    }

    public static class Builder extends TransactionCoordinatorTask.Builder<Builder> {

        @Override
        protected Builder getThis() {
            return this;
        }

        @Override
        public TransactionCoordinatorTask<Builder> build() {
            return new AbortTask(this);
        }
    }

}
