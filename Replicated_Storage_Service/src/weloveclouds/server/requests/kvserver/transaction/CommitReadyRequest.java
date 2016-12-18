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

public class CommitReadyRequest extends AbstractRequest<CommitReadyRequest.Builder> {

    private static final Logger LOGGER = Logger.getLogger(CommitReadyRequest.class);

    private IRequestFactory<IKVTransferMessage, IKVTransferRequest> simulatedDASBehavior;

    protected CommitReadyRequest(Builder builder) {
        super(builder);
        this.simulatedDASBehavior = builder.simulatedDASBehavior;
    }

    @Override
    public IKVTransactionMessage execute() {
        if (!transactionLog.containsKey(transactionId)
                || !ongoingTransactions.containsKey(transactionId)) {
            return createUnknownIDTransactionResponse(transactionId);
        } else {
            TransactionStatus recentStatus = transactionLog.get(transactionId);
            IKVTransactionMessage transactionMessage = ongoingTransactions.get(transactionId);
            // TODO handle if status is HELP_NEEDED
            switch (recentStatus) {
                case INIT:
                    try {
                        simulatedDASBehavior.createRequestFromReceivedMessage(
                                transactionMessage.getTransferPayload(), null);
                        transactionLog.put(transactionId, TransactionStatus.COMMIT_READY);
                        return createTransactionResponse(transactionId,
                                StatusType.RESPONSE_COMMIT_READY);
                    } catch (Exception ex) {
                        LOGGER.error(ex);
                        return createTransactionResponse(transactionId,
                                StatusType.RESPONSE_ABORTED);
                    }
                case COMMIT_READY:
                    return createTransactionResponse(transactionId,
                            StatusType.RESPONSE_COMMIT_READY);
                default:
                    return createTransactionResponse(transactionId, StatusType.RESPONSE_ABORTED);
            }
        }
    }

    public static class Builder extends AbstractRequest.Builder<Builder> {

        private IRequestFactory<IKVTransferMessage, IKVTransferRequest> simulatedDASBehavior;

        public Builder simulatedDASBehavior(
                IRequestFactory<IKVTransferMessage, IKVTransferRequest> simulatedDASBehavior) {
            this.simulatedDASBehavior = simulatedDASBehavior;
            return this;
        }

        public CommitReadyRequest build() {
            return new CommitReadyRequest(this);
        }
    }

}
