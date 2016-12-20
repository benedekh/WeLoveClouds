package weloveclouds.server.requests.kvserver.transaction;

import static weloveclouds.server.requests.kvserver.transaction.utils.KVTransactionMessageFactory.createTransactionResponse;
import static weloveclouds.server.requests.kvserver.transaction.utils.KVTransactionMessageFactory.createUnknownIDTransactionResponse;

import java.util.Set;

import org.apache.log4j.Logger;

import weloveclouds.commons.exceptions.IllegalRequestException;
import weloveclouds.commons.kvstore.models.messages.IKVTransactionMessage;
import weloveclouds.commons.kvstore.models.messages.IKVTransactionMessage.StatusType;
import weloveclouds.commons.kvstore.models.messages.IKVTransferMessage;
import weloveclouds.commons.utils.StringUtils;
import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.server.requests.kvserver.transaction.models.ReceivedTransactionContext;
import weloveclouds.server.requests.kvserver.transaction.models.TransactionStatus;
import weloveclouds.server.requests.kvserver.transaction.utils.TimedAbort;
import weloveclouds.server.requests.kvserver.transaction.utils.TimedHelp;
import weloveclouds.server.services.transaction.TransactionServiceFactory;

public class InitRequest extends AbstractRequest<InitRequest.Builder> {

    private static final Logger LOGGER = Logger.getLogger(InitRequest.class);

    private IKVTransferMessage transferMessage;
    private Set<ServerConnectionInfo> otherParticipants;
    private TransactionServiceFactory transactionServiceFactory;

    protected InitRequest(Builder builder) {
        super(builder);
        this.transferMessage = builder.transferMessage;
        this.otherParticipants = builder.otherParticipants;
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
        if (transferMessage == null) {
            LOGGER.error("Transfer message is null.");
            throw new IllegalRequestException(createUnknownIDTransactionResponse(null));
        }
        return this;
    }

    private TimedAbort createTimedAbortRequest(ReceivedTransactionContext transaction) {
        return new TimedAbort(transaction);
    }

    private TimedHelp createTimedHelpRequest(ReceivedTransactionContext transaction) {
        return new TimedHelp.Builder().transaction(transaction)
                .transactionServiceFactory(transactionServiceFactory).build();
    }

    private ReceivedTransactionContext createTransactionContext() {
        return new ReceivedTransactionContext.Builder().transactionId(transactionId)
                .transactionStatus(TransactionStatus.INIT).otherParticipants(otherParticipants)
                .transferMessage(transferMessage).build();
    }

    public static class Builder extends AbstractRequest.Builder<Builder> {
        private IKVTransferMessage transferMessage;
        private Set<ServerConnectionInfo> otherParticipants;
        private TransactionServiceFactory transactionServiceFactory;

        public Builder transferMessage(IKVTransferMessage transferMessage) {
            this.transferMessage = transferMessage;
            return this;
        }

        public Builder otherParticipants(Set<ServerConnectionInfo> otherParticipants) {
            this.otherParticipants = otherParticipants;
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
