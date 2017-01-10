package weloveclouds.server.requests.kvserver.transaction;

import java.util.Map;
import java.util.UUID;

import org.apache.log4j.Logger;

import weloveclouds.commons.kvstore.models.messages.IKVTransactionMessage;
import weloveclouds.commons.kvstore.models.messages.IKVTransactionMessage.StatusType;
import weloveclouds.commons.kvstore.models.messages.IKVTransferMessage;
import weloveclouds.commons.networking.models.requests.ICallbackRegister;
import weloveclouds.commons.networking.models.requests.IRequestFactory;
import weloveclouds.commons.utils.StringUtils;
import weloveclouds.server.requests.kvserver.transaction.models.ReceivedTransactionContext;
import weloveclouds.server.requests.kvserver.transfer.IKVTransferRequest;
import weloveclouds.server.services.transaction.TransactionServiceFactory;

/**
 * A service which is responsible for receiving transactions on the KVServer side and handling them
 * according to the status information in the transaction.
 * 
 * @author Benedek
 */
public class TransactionReceiverService
        implements IRequestFactory<IKVTransactionMessage, IKVTransactionRequest> {

    private static final Logger LOGGER = Logger.getLogger(TransactionReceiverService.class);

    private Map<UUID, ReceivedTransactionContext> transactionLog;
    private TransactionServiceFactory transactionServiceFactory;

    private IRequestFactory<IKVTransferMessage, IKVTransferRequest> simulatedDASBehavior;
    private IRequestFactory<IKVTransferMessage, IKVTransferRequest> realDASBehavior;

    protected TransactionReceiverService(Builder builder) {
        this.transactionLog = builder.transactionLog;
        this.transactionServiceFactory = builder.transactionServiceFactory;
        this.simulatedDASBehavior = builder.simulatedDASBehavior;
        this.realDASBehavior = builder.realDASBehavior;
    }

    @Override
    public IKVTransactionRequest createRequestFromReceivedMessage(
            IKVTransactionMessage receivedMessage, ICallbackRegister callbackRegister) {
        StatusType status = receivedMessage.getStatus();
        LOGGER.debug(StringUtils.join("", "Request status: ", status));
        IKVTransactionRequest request = null;

        switch (status) {
            case ABORT:
                request = new AbortRequest.Builder().transactionLog(transactionLog)
                        .transactionId(receivedMessage.getTransactionId()).build();
                break;
            case INIT:
                request = new InitRequest.Builder().transactionLog(transactionLog)
                        .transactionId(receivedMessage.getTransactionId())
                        .transactionMessage(receivedMessage).realDASBehavior(realDASBehavior)
                        .transactionServiceFactory(transactionServiceFactory).build();
                break;
            case COMMIT:
                request = new CommitRequest.Builder().transactionLog(transactionLog)
                        .transactionId(receivedMessage.getTransactionId())
                        .realDASBehavior(realDASBehavior).build();
                break;
            case COMMIT_READY:
                request = new CommitReadyRequest.Builder().transactionLog(transactionLog)
                        .transactionId(receivedMessage.getTransactionId())
                        .simulatedDASBehavior(simulatedDASBehavior).build();
                break;
            default:
                request = new HelpRequest.Builder().transactionLog(transactionLog)
                        .transactionId(receivedMessage.getTransactionId()).build();
                break;
        }

        return request;
    }

    /**
     * Builder pattern for creating a {@link TransactionReceiverService} instance.
     *
     * @author Benedek
     */
    public static class Builder {
        private Map<UUID, ReceivedTransactionContext> transactionLog;
        private TransactionServiceFactory transactionServiceFactory;
        private IRequestFactory<IKVTransferMessage, IKVTransferRequest> simulatedDASBehavior;
        private IRequestFactory<IKVTransferMessage, IKVTransferRequest> realDASBehavior;

        public Builder transactionLog(Map<UUID, ReceivedTransactionContext> transactionLog) {
            this.transactionLog = transactionLog;
            return this;
        }

        public Builder simulatedDASBehavior(
                IRequestFactory<IKVTransferMessage, IKVTransferRequest> simulatedDASBehavior) {
            this.simulatedDASBehavior = simulatedDASBehavior;
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

        public TransactionReceiverService build() {
            return new TransactionReceiverService(this);
        }
    }


}
