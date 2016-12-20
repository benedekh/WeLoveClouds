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
import weloveclouds.server.requests.kvserver.transaction.models.ReceivedTransactionContext;
import weloveclouds.server.services.transaction.tasks.ITransactionTask;

public class CommitOrAbortDecisionTask implements ITransactionTask {

    private static final Logger LOGGER = Logger.getLogger(CommitOrAbortDecisionTask.class);

    private ReceivedTransactionContext transaction;
    private Set<Future<StatusType>> responseFutures;

    public CommitOrAbortDecisionTask(ReceivedTransactionContext transaction,
            Set<Future<StatusType>> responseFutures) {
        this.transaction = transaction;
        this.responseFutures = responseFutures;
    }

    @Override
    public void execute() throws InterruptedException, ExecutionException {
        LOGGER.error(StringUtils.join("", "CommitOrAbortDecision phase for transaction (",
                transaction.getTransactionId(), ") started."));

        Set<StatusType> responses = getResponses();
        if (responsesContainCommitted(responses)) {
            transaction.setCommitted();
            LOGGER.debug(StringUtils.join("", "Transaction (", transaction.getTransactionId(),
                    ") is set committed."));
        } else if (responsesContainAborted(responses)) {
            transaction.setAborted();
            LOGGER.debug(StringUtils.join("", "Transaction (", transaction.getTransactionId(),
                    ") is set aborted."));
        } else {
            LOGGER.error(StringUtils.join("", "Transaction (", transaction.getTransactionId(),
                    ") is neither committed nor aborted."));
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

}
