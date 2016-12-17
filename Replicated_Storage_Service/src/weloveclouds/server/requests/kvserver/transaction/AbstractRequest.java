package weloveclouds.server.requests.kvserver.transaction;

import static weloveclouds.server.requests.kvserver.transaction.utils.KVTransactionMessageFactory.createUnknownIDTransactionResponse;

import java.util.Map;
import java.util.UUID;

import org.apache.log4j.Logger;

import weloveclouds.commons.exceptions.IllegalRequestException;
import weloveclouds.commons.kvstore.models.messages.IKVTransactionMessage;
import weloveclouds.server.requests.kvserver.transaction.utils.TransactionStatus;

public abstract class AbstractRequest<E extends AbstractRequest.Builder<E>>
        implements IKVTransactionRequest {

    private static Logger LOGGER = Logger.getLogger(AbstractRequest.class);

    protected UUID transactionId;
    protected Map<UUID, TransactionStatus> transactionLog;
    protected Map<UUID, IKVTransactionMessage> ongoingTransactions;

    protected AbstractRequest(Builder<E> builder) {
        this.transactionLog = builder.transactionLog;
        this.ongoingTransactions = builder.ongoingTransactions;
        this.transactionId = builder.transactionId;
    }

    @Override
    public IKVTransactionRequest validate() throws IllegalArgumentException {
        if (transactionId == null) {
            LOGGER.error("Transaction ID cannot be null.");
            throw new IllegalRequestException(createUnknownIDTransactionResponse(null));
        }
        return this;
    }

    public abstract static class Builder<E extends Builder<E>> {
        private UUID transactionId;
        private Map<UUID, TransactionStatus> transactionLog;
        private Map<UUID, IKVTransactionMessage> ongoingTransactions;

        public Builder<E> transactionId(UUID transactionId) {
            this.transactionId = transactionId;
            return getThis();
        }

        public Builder<E> transactionLog(Map<UUID, TransactionStatus> transactionLog) {
            this.transactionLog = transactionLog;
            return getThis();
        }

        public Builder<E> ongoingTransactions(
                Map<UUID, IKVTransactionMessage> ongoingTransactions) {
            this.ongoingTransactions = ongoingTransactions;
            return getThis();
        }

        protected abstract E getThis();

        public abstract AbstractRequest<E> build();
    }

}
