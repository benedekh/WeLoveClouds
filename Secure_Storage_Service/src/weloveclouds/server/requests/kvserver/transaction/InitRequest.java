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
import weloveclouds.server.requests.kvserver.transaction.models.ReceivedTransactionContext;
import weloveclouds.server.requests.kvserver.transaction.models.TransactionStatus;
import weloveclouds.server.requests.kvserver.transaction.utils.TimedAbort;
import weloveclouds.server.requests.kvserver.transaction.utils.TimedHelp;
import weloveclouds.server.requests.kvserver.transfer.IKVTransferRequest;
import weloveclouds.server.services.transaction.TransactionServiceFactory;

/**
 * Represents the initial phase of a transaction.
 * 
 * @author Benedek
 */
public class InitRequest extends AbstractRequest<InitRequest.Builder> {

    private static final Logger LOGGER = Logger.getLogger(InitRequest.class);

    private IKVTransactionMessage transactionMessage;
    private IRequestFactory<IKVTransferMessage, IKVTransferRequest> realDASBehavior;
    private TransactionServiceFactory transactionServiceFactory;

    protected InitRequest(Builder builder) {
        super(builder);
        this.transactionMessage = builder.transactionMessage;
        this.realDASBehavior = builder.realDASBehavior;
        this.transactionServiceFactory = builder.transactionServiceFactory;
    }

    @Override
    public IKVTransactionMessage execute() {
        LOGGER.debug(StringUtils.join("", "Init phase for transaction (", transactionId,
                ") on receiver side."));

        if (!transactionLog.containsKey(transactionId)) {
            synchronized (transactionLog) {
                if (!transactionLog.containsKey(transactionId)) {
                    ReceivedTransactionContext transaction = createTransactionContext();
                    transaction.setTimedRestoration(createTimedHelpRequest(transaction));
                    transactionLog.put(transactionId, transaction);
                    transaction.scheduleTimedRestoration();
                }
            }
        } else {
            TransactionStatus recentStatus =
                    transactionLog.get(transactionId).getTransactionStatus();
            if (recentStatus != TransactionStatus.INIT) {
                LOGGER.debug(StringUtils.join("", recentStatus, " for transaction (", transactionId,
                        ") on receiver side."));
                return createTransactionResponse(transactionId,
                        StatusType.RESPONSE_GENERATE_NEW_ID);
            }
        }

        LOGGER.debug(StringUtils.join("", "Init_Ready for transaction (", transactionId,
                ") on receiver side."));
        return createTransactionResponse(transactionId, StatusType.RESPONSE_INIT_READY);
    }

    @Override
    public IKVTransactionRequest validate() throws IllegalArgumentException {
        super.validate();
        if (transactionMessage == null) {
            LOGGER.error("Transaction message is null.");
            throw new IllegalRequestException(createUnknownIDTransactionResponse(null));
        }
        if (realDASBehavior == null) {
            LOGGER.error("Real data access service is null.");
            throw new IllegalRequestException(createUnknownIDTransactionResponse(null));
        }
        return this;
    }

    private TimedAbort createTimedAbortRequest() {
        return new TimedAbort(createAbortRequest());
    }

    private TimedHelp createTimedHelpRequest(ReceivedTransactionContext transaction) {
        return new TimedHelp.Builder().abortRequest(createAbortRequest())
                .commitRequest(createCommitRequest()).transaction(transaction)
                .transactionServiceFactory(transactionServiceFactory).build();
    }

    protected AbortRequest createAbortRequest() {
        return new AbortRequest.Builder().transactionId(transactionId)
                .transactionLog(transactionLog).build();
    }

    protected CommitRequest createCommitRequest() {
        return new CommitRequest.Builder().transactionId(transactionId)
                .transactionLog(transactionLog).realDASBehavior(realDASBehavior).build();
    }

    private ReceivedTransactionContext createTransactionContext() {
        return new ReceivedTransactionContext.Builder().transactionId(transactionId)
                .transactionStatus(TransactionStatus.INIT)
                .otherParticipants(transactionMessage.getOtherParticipants())
                .transferMessage(transactionMessage.getTransferPayload()).build();
    }

    /**
     * Builder pattern for creating a {@link InitRequest} instance.
     *
     * @author Benedek
     */
    public static class Builder extends AbstractRequest.Builder<Builder> {
        private IKVTransactionMessage transactionMessage;
        private IRequestFactory<IKVTransferMessage, IKVTransferRequest> realDASBehavior;
        private TransactionServiceFactory transactionServiceFactory;

        public Builder transactionMessage(IKVTransactionMessage transactionMessage) {
            this.transactionMessage = transactionMessage;
            return this;
        }

        public Builder realDASBehavior(
                IRequestFactory<IKVTransferMessage, IKVTransferRequest> realDASBehavior) {
            this.realDASBehavior = realDASBehavior;
            return this;
        }

        public Builder transactionServiceFactory(
                TransactionServiceFactory transactionServiceFactory) {
            this.transactionServiceFactory = transactionServiceFactory;
            return this;
        }

        public InitRequest build() {
            return new InitRequest(this);
        }
    }
}
