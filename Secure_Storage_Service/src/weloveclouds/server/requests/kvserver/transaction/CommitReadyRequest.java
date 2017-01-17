package weloveclouds.server.requests.kvserver.transaction;

import static weloveclouds.server.requests.kvserver.transaction.utils.KVTransactionMessageFactory.createTransactionResponse;
import static weloveclouds.server.requests.kvserver.transaction.utils.KVTransactionMessageFactory.createUnknownIDTransactionResponse;

import org.apache.log4j.Logger;

import weloveclouds.commons.exceptions.IllegalRequestException;
import weloveclouds.commons.kvstore.models.messages.IKVTransactionMessage;
import weloveclouds.commons.kvstore.models.messages.IKVTransactionMessage.StatusType;
import weloveclouds.commons.kvstore.models.messages.IKVTransferMessage;
import weloveclouds.commons.networking.models.requests.IRequestFactory;
import weloveclouds.commons.utils.CloseableLock;
import weloveclouds.commons.utils.StringUtils;
import weloveclouds.server.requests.kvserver.transaction.models.ReceivedTransactionContext;
import weloveclouds.server.requests.kvserver.transaction.models.TransactionStatus;
import weloveclouds.server.requests.kvserver.transfer.IKVTransferRequest;
import weloveclouds.server.store.exceptions.StorageException;

/**
 * Represents the commit ready phase of a transaction.
 * 
 * 
 * @author Benedek
 */
public class CommitReadyRequest extends AbstractRequest<CommitReadyRequest.Builder> {

    private static final Logger LOGGER = Logger.getLogger(CommitReadyRequest.class);

    private IRequestFactory<IKVTransferMessage, IKVTransferRequest> simulatedDASBehavior;

    protected CommitReadyRequest(Builder builder) {
        super(builder);
        this.simulatedDASBehavior = builder.simulatedDASBehavior;
    }

    @Override
    public IKVTransactionMessage execute() {
        LOGGER.debug(StringUtils.join("", "Commit_ready phase for transaction (", transactionId,
                ") on receiver side."));

        ReceivedTransactionContext transaction = transactionLog.get(transactionId);
        TransactionStatus recentStatus = transaction.getTransactionStatus();
        switch (recentStatus) {
            case INIT:
                try {
                    try (CloseableLock lock = new CloseableLock(transaction.getLock())) {
                        if (transaction.getTransactionStatus() != TransactionStatus.COMMIT_READY) {
                            IKVTransferMessage transferMessage = transaction.getTransferMessage();
                            if (transferMessage != null) {
                                IKVTransferMessage virtualResponse =
                                        simulatedDASBehavior
                                                .createRequestFromReceivedMessage(transferMessage,
                                                        new EmptyCallbackRegister())
                                                .validate().execute();
                                switch (virtualResponse.getStatus()) {
                                    case RESPONSE_ERROR:
                                        throw new StorageException(
                                                virtualResponse.getResponseMessage());
                                    default:
                                        break;
                                }
                                transaction.setCommitReady();
                            }
                            LOGGER.debug(StringUtils.join("", "Commit_Ready for transaction (",
                                    transactionId, ") on reciever side."));
                            return createTransactionResponse(transactionId,
                                    StatusType.RESPONSE_COMMIT_READY);
                        }
                    }
                } catch (Exception ex) {
                    LOGGER.error(ex);
                    return new AbortRequest.Builder().transactionLog(transactionLog)
                            .transactionId(transactionId).build().execute();
                }
            default:
                LOGGER.debug(StringUtils.join("", recentStatus, " for transaction (", transactionId,
                        ") on receiver side."));
                return createTransactionResponse(transactionId, recentStatus.getResponseType());
        }
    }

    @Override
    public IKVTransactionRequest validate() throws IllegalArgumentException {
        super.validate();
        if (!transactionLog.containsKey(transactionId)) {
            LOGGER.error(StringUtils.join("", "Unknown transaction ID: ", transactionId));
            throw new IllegalRequestException(createUnknownIDTransactionResponse(transactionId));
        }
        return this;
    }

    /**
     * Builder pattern for creating a {@link CommitReadyRequest} instance.
     *
     * @author Benedek
     */
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
