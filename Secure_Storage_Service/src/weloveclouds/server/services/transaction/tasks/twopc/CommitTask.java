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
 * Commit task for a transaction. Simply sends the COMMIT message to every participant.
 * 
 * @author Benedek
 */
public class CommitTask extends TransactionTask<CommitTask.Builder> {

    private static final Logger LOGGER = Logger.getLogger(CommitTask.class);

    protected CommitTask(Builder builder) {
        super(builder);
    }

    @Override
    protected void executeTask() throws InterruptedException, ExecutionException {
        LOGGER.debug("Commit phase for transactions started.");
        Set<Future<StatusType>> futures = sendCommitRequests();
        if (notEveryoneIsCommitted(futures)) {
            throw new ExecutionException(new Exception("Not every participant is committed."));
        }
        LOGGER.debug("Commit phase for transactions finished.");
    }

    private Set<Future<StatusType>> sendCommitRequests() {
        Set<Future<StatusType>> futures = new HashSet<>();
        for (SenderTransaction transaction : transactions) {
            futures.add(executorService.submit(transaction.new Commit()));
        }
        return futures;
    }

    private boolean notEveryoneIsCommitted(Set<Future<StatusType>> responses)
            throws InterruptedException, ExecutionException {
        return !everyonesHasTheExpectedStatus(responses, StatusType.RESPONSE_COMMITTED);
    }

    /**
     * Builder pattern for creating a {@link CommitTask} instance.
     *
     * @author Benedek
     */
    public static class Builder extends TransactionTask.Builder<Builder> {

        public CommitTask build() {
            return new CommitTask(this);
        }
    }

}
