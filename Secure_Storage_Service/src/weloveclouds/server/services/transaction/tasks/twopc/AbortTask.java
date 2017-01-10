package weloveclouds.server.services.transaction.tasks.twopc;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;

import weloveclouds.commons.kvstore.models.messages.IKVTransactionMessage.StatusType;
import weloveclouds.server.services.transaction.SenderTransaction;
import weloveclouds.server.services.transaction.tasks.TransactionTask;

/**
 * Abort task for a transaction. Simply sends the ABORT message to every participant.
 * 
 * @author Benedek
 */
public class AbortTask extends TransactionTask<AbortTask.Builder> {

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

    /**
     * Builder pattern for creating a {@link Abort} instance.
     *
     * @author Benedek
     */
    public static class Builder extends TransactionTask.Builder<Builder> {

        public AbortTask build() {
            return new AbortTask(this);
        }
    }

}
