package weloveclouds.server.requests.kvserver.transaction;

import static weloveclouds.server.requests.kvserver.transaction.utils.KVTransactionMessageFactory.createTransactionResponse;
import static weloveclouds.server.requests.kvserver.transaction.utils.KVTransactionMessageFactory.createUnknownIDTransactionResponse;

import org.apache.log4j.Logger;

import weloveclouds.commons.exceptions.IllegalRequestException;
import weloveclouds.commons.kvstore.models.messages.IKVTransactionMessage;
import weloveclouds.commons.kvstore.models.messages.IKVTransactionMessage.StatusType;
import weloveclouds.commons.kvstore.models.messages.IKVTransferMessage;
import weloveclouds.commons.networking.models.requests.IRequestFactory;
import weloveclouds.commons.utils.StringUtils;
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
        LOGGER.debug(StringUtils.join("", "Commit_ready phase for transaction (", transactionId,
                ") on reciever side."));

        synchronized (transactionLog) {
            TransactionStatus recentStatus = transactionLog.get(transactionId);
            switch (recentStatus) {
                case INIT:
                    try {
                        IKVTransferMessage transferMessage = ongoingTransactions.get(transactionId);
                        if (transferMessage != null) {
                            simulatedDASBehavior.createRequestFromReceivedMessage(transferMessage,
                                    new EmptyCallbackRegister());
                            transactionLog.put(transactionId, TransactionStatus.COMMIT_READY);
                        }
                        LOGGER.debug(StringUtils.join("", "Commit_Ready for transaction (",
                                transactionId, ") on reciever side."));
                        return createTransactionResponse(transactionId,
                                StatusType.RESPONSE_COMMIT_READY);
                    } catch (Exception ex) {
                        LOGGER.error(ex);
                        return new AbortRequest.Builder().transactionLog(transactionLog)
                                .ongoingTransactions(ongoingTransactions)
                                .transactionId(transactionId).build().execute();
                    }
                default:
                    LOGGER.debug(StringUtils.join("", recentStatus, " for transaction (",
                            transactionId, ") on reciever side."));
                    return createTransactionResponse(transactionId, recentStatus.getResponseType());
            }
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
