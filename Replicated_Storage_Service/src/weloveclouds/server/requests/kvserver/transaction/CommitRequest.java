package weloveclouds.server.requests.kvserver.transaction;

import static weloveclouds.server.requests.kvserver.transaction.utils.KVTransactionMessageFactory.createTransactionResponse;
import static weloveclouds.server.requests.kvserver.transaction.utils.KVTransactionMessageFactory.createUnknownIDTransactionResponse;

import org.apache.log4j.Logger;

import weloveclouds.commons.kvstore.models.messages.IKVTransactionMessage;
import weloveclouds.commons.kvstore.models.messages.IKVTransactionMessage.StatusType;
import weloveclouds.commons.kvstore.models.messages.IKVTransferMessage;
import weloveclouds.commons.networking.models.requests.IRequestFactory;
import weloveclouds.server.requests.kvserver.transaction.utils.TransactionStatus;
import weloveclouds.server.requests.kvserver.transfer.IKVTransferRequest;

public class CommitRequest extends AbstractRequest<CommitRequest.Builder> {

    private static final Logger LOGGER = Logger.getLogger(CommitRequest.class);

    private IRequestFactory<IKVTransferMessage, IKVTransferRequest> realDASBehavior;

    protected CommitRequest(Builder builder) {
        super(builder);
        this.realDASBehavior = builder.realDASBehavior;
    }

    @Override
    public IKVTransactionMessage execute() {
        if (!transactionLog.containsKey(transactionId)) {
            return createUnknownIDTransactionResponse(transactionId);
        } else {
            TransactionStatus recentStatus = transactionLog.get(transactionId);

            // TODO handle if status is HELP_NEEDED
            if (recentStatus == TransactionStatus.COMMITTED) {
                return createTransactionResponse(transactionId, StatusType.RESPONSE_COMMITTED);
            } else {
                IKVTransactionMessage transactionMessage = ongoingTransactions.get(transactionId);
                switch (recentStatus) {
                    case COMMIT_READY:
                        try {
                            realDASBehavior.createRequestFromReceivedMessage(
                                    transactionMessage.getTransferPayload(), null);
                            transactionLog.put(transactionId, TransactionStatus.COMMITTED);
                            return createTransactionResponse(transactionId,
                                    StatusType.RESPONSE_COMMITTED);
                        } catch (Exception ex) {
                            LOGGER.error(ex);
                            transactionLog.put(transactionId, TransactionStatus.ABORTED);
                            return createTransactionResponse(transactionId,
                                    StatusType.RESPONSE_ABORTED);
                        } finally {
                            ongoingTransactions.remove(transactionId);
                        }
                    default:
                        return createTransactionResponse(transactionId,
                                StatusType.RESPONSE_ABORTED);
                }
            }
        }
    }

    public static class Builder extends AbstractRequest.Builder<Builder> {
        private IRequestFactory<IKVTransferMessage, IKVTransferRequest> realDASBehavior;

        public Builder realDASBehavior(
                IRequestFactory<IKVTransferMessage, IKVTransferRequest> realDASBehavior) {
            this.realDASBehavior = realDASBehavior;
            return this;
        }

        public CommitRequest build() {
            return new CommitRequest(this);
        }
    }
}
