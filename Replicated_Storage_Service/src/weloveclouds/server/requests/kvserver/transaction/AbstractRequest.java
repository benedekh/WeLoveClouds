package weloveclouds.server.requests.kvserver.transaction;

import static weloveclouds.server.requests.kvserver.transaction.utils.KVTransactionMessageFactory.createUnknownIDTransactionResponse;

import java.util.Map;
import java.util.UUID;

import org.apache.log4j.Logger;

import weloveclouds.commons.exceptions.IllegalRequestException;
import weloveclouds.commons.kvstore.models.messages.IKVTransferMessage;
import weloveclouds.commons.networking.models.requests.ICallbackRegister;
import weloveclouds.server.requests.kvserver.transaction.utils.TimedAbortRequest;
import weloveclouds.server.requests.kvserver.transaction.utils.TransactionStatus;

public abstract class AbstractRequest<E extends AbstractRequest.Builder<E>>
        implements IKVTransactionRequest {

    private static Logger LOGGER = Logger.getLogger(AbstractRequest.class);

    protected UUID transactionId;
    protected Map<UUID, TransactionStatus> transactionLog;
    protected Map<UUID, IKVTransferMessage> ongoingTransactions;
    protected Map<UUID, TimedAbortRequest> timedAbortRequests;

    protected AbstractRequest(Builder<E> builder) {
        this.transactionLog = builder.transactionLog;
        this.ongoingTransactions = builder.ongoingTransactions;
        this.transactionId = builder.transactionId;
        this.timedAbortRequests = builder.timedAbortRequests;
    }

    @Override
    public IKVTransactionRequest validate() throws IllegalArgumentException {
        if (transactionId == null) {
            LOGGER.error("Transaction ID cannot be null.");
            throw new IllegalRequestException(createUnknownIDTransactionResponse(null));
        }
        return this;
    }

    protected void haltTimedAbortRequest() {
        TimedAbortRequest timedAbortRequest = timedAbortRequests.get(transactionId);
        if (timedAbortRequest != null) {
            timedAbortRequests.remove(transactionId);
            timedAbortRequest.interrupt();
        }
    }

    public abstract static class Builder<E extends Builder<E>> {
        private UUID transactionId;
        private Map<UUID, TransactionStatus> transactionLog;
        private Map<UUID, IKVTransferMessage> ongoingTransactions;
        private Map<UUID, TimedAbortRequest> timedAbortRequests;

        @SuppressWarnings("unchecked")
        public E transactionId(UUID transactionId) {
            this.transactionId = transactionId;
            return (E) this;
        }

        @SuppressWarnings("unchecked")
        public E transactionLog(Map<UUID, TransactionStatus> transactionLog) {
            this.transactionLog = transactionLog;
            return (E) this;
        }

        @SuppressWarnings("unchecked")
        public E ongoingTransactions(Map<UUID, IKVTransferMessage> ongoingTransactions) {
            this.ongoingTransactions = ongoingTransactions;
            return (E) this;
        }

        @SuppressWarnings("unchecked")
        public E timedAbortRequests(Map<UUID, TimedAbortRequest> timedAbortRequests) {
            this.timedAbortRequests = timedAbortRequests;
            return (E) this;
        }
    }

    protected class EmptyCallbackRegister implements ICallbackRegister {

        @Override
        public void registerCallback(Runnable callback) {
            // left empty on purpose
        }

    }

}
