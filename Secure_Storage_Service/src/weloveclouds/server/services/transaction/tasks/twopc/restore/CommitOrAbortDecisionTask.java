package weloveclouds.server.services.transaction.tasks.twopc.restore;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;

import weloveclouds.commons.kvstore.models.messages.IKVTransactionMessage.StatusType;
import weloveclouds.commons.utils.StringUtils;
import weloveclouds.server.requests.kvserver.transaction.AbortRequest;
import weloveclouds.server.requests.kvserver.transaction.CommitRequest;
import weloveclouds.server.services.transaction.tasks.ITransactionTask;

/**
 * Decides if the transaction shall be committed or aborted, based on the replies received from
 * other participants.
 * 
 * @author Benedek
 */
public class CommitOrAbortDecisionTask implements ITransactionTask {

    private static final Logger LOGGER = Logger.getLogger(CommitOrAbortDecisionTask.class);

    private AbortRequest abortRequest;
    private CommitRequest commitRequest;
    private Set<Future<StatusType>> responseFutures;

    protected CommitOrAbortDecisionTask(Builder builder) {
        this.abortRequest = builder.abortRequest;
        this.commitRequest = builder.commitRequest;
        this.responseFutures = builder.responseFutures;
    }

    @Override
    public void execute() throws InterruptedException, ExecutionException {
        LOGGER.error(StringUtils.join("", "CommitOrAbortDecision phase for transaction started."));

        Set<StatusType> responses = getResponses();
        if (responsesContainCommitted(responses)) {
            commitRequest.execute();
        } else if (responsesContainAborted(responses)) {
            abortRequest.execute();
        } else {
            LOGGER.error(StringUtils.join("", "Transaction is neither committed nor aborted."));
        }
    }

    private Set<StatusType> getResponses() {
        Set<StatusType> responses = new HashSet<>();
        for (Future<StatusType> responseFuture : responseFutures) {
            try {
                responses.add(responseFuture.get(10, TimeUnit.MILLISECONDS));
            } catch (TimeoutException ex) {
                // response not arrived beforehand
            } catch (Exception ex) {
                LOGGER.error(ex);
            }
        }
        return responses;
    }

    private boolean responsesContainCommitted(Set<StatusType> responses) {
        return responses.contains(StatusType.RESPONSE_COMMITTED);
    }

    private boolean responsesContainAborted(Set<StatusType> responses) {
        return responses.contains(StatusType.RESPONSE_ABORTED);
    }

    /**
     * Builder pattern for creating a {@link CommitOrAbortDecisionTask} instance.
     *
     * @author Benedek
     */
    public static class Builder {
        private AbortRequest abortRequest;
        private CommitRequest commitRequest;
        private Set<Future<StatusType>> responseFutures;

        public Builder abortRequest(AbortRequest abortRequest) {
            this.abortRequest = abortRequest;
            return this;
        }

        public Builder commitRequest(CommitRequest commitRequest) {
            this.commitRequest = commitRequest;
            return this;
        }

        public Builder responseFutures(Set<Future<StatusType>> responseFutures) {
            this.responseFutures = responseFutures;
            return this;
        }

        public CommitOrAbortDecisionTask build() {
            return new CommitOrAbortDecisionTask(this);
        }

    }

}
