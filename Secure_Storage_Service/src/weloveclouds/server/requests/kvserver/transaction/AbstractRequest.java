package weloveclouds.server.requests.kvserver.transaction;

import static weloveclouds.server.requests.kvserver.transaction.utils.KVTransactionMessageFactory.createUnknownIDTransactionResponse;

import java.util.Map;
import java.util.UUID;

import org.apache.log4j.Logger;

import weloveclouds.commons.exceptions.IllegalRequestException;
import weloveclouds.commons.networking.models.requests.ICallbackRegister;
import weloveclouds.server.requests.kvserver.transaction.models.ReceivedTransactionContext;

/**
 * Represents an abstract transaction request.
 * 
 * @author Benedek
 * 
 * @param <E> type of the builder that is called runtime
 */
public abstract class AbstractRequest<E extends AbstractRequest.Builder<E>>
        implements IKVTransactionRequest {

    private static Logger LOGGER = Logger.getLogger(AbstractRequest.class);

    protected UUID transactionId;
    protected Map<UUID, ReceivedTransactionContext> transactionLog;

    protected AbstractRequest(Builder<E> builder) {
        this.transactionId = builder.transactionId;
        this.transactionLog = builder.transactionLog;
    }

    @Override
    public IKVTransactionRequest validate() throws IllegalArgumentException {
        if (transactionId == null) {
            LOGGER.error("Transaction ID cannot be null.");
            throw new IllegalRequestException(createUnknownIDTransactionResponse(null));
        }
        return this;
    }

    /**
     * Builder pattern for creating a {@link AbstractRequest} instance.
     *
     * @author Benedek
     */
    public abstract static class Builder<E extends Builder<E>> {
        private UUID transactionId;
        private Map<UUID, ReceivedTransactionContext> transactionLog;

        @SuppressWarnings("unchecked")
        public E transactionId(UUID transactionId) {
            this.transactionId = transactionId;
            return (E) this;
        }

        @SuppressWarnings("unchecked")
        public E transactionLog(Map<UUID, ReceivedTransactionContext> transactionLog) {
            this.transactionLog = transactionLog;
            return (E) this;
        }
    }

    /**
     * Helper class which does nothing.
     * 
     * @author Benedek
     */
    protected class EmptyCallbackRegister implements ICallbackRegister {

        @Override
        public void registerCallback(Runnable callback) {
            // left empty on purpose
        }

    }

}
