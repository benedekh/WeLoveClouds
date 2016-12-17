package weloveclouds.server.services.transaction.tasks.twopc;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;

import weloveclouds.commons.kvstore.models.messages.IKVTransactionMessage.StatusType;
import weloveclouds.server.services.transaction.SenderTransaction;
import weloveclouds.server.services.transaction.tasks.TransactionCoordinatorTask;

public class CommitReadyTask extends TransactionCoordinatorTask<CommitReadyTask.Builder> {

    private static final Logger LOGGER = Logger.getLogger(CommitReadyTask.class);

    protected CommitReadyTask(Builder builder) {
        super(builder);
    }

    @Override
    protected void executeTask() throws InterruptedException, ExecutionException {
        LOGGER.debug("Commit ready phase for transactions started.");
        Set<Future<StatusType>> futures = sendCommitReadyRequests();
        if (notEveryoneIsCommitReady(futures)) {
            throw new ExecutionException(
                    new Exception("Not every participant is ready for commit."));
        }
        LOGGER.debug("Commit ready phase for transactions finished.");
    }

    private Set<Future<StatusType>> sendCommitReadyRequests() {
        Set<Future<StatusType>> futures = new HashSet<>();
        for (SenderTransaction transaction : transactions) {
            futures.add(executorService.submit(transaction.new CommitReady()));
        }
        return futures;
    }

    private boolean notEveryoneIsCommitReady(Set<Future<StatusType>> responses)
            throws InterruptedException, ExecutionException {
        return !everyonesHasTheExpectedStatus(responses, StatusType.RESPONSE_COMMIT_READY);
    }

    public static class Builder extends TransactionCoordinatorTask.Builder<Builder> {

        @Override
        protected Builder getThis() {
            return this;
        }

        @Override
        public TransactionCoordinatorTask<Builder> build() {
            return new CommitReadyTask(this);
        }
    }

}
