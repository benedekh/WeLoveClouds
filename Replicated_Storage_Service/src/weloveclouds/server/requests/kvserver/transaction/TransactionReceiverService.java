package weloveclouds.server.requests.kvserver.transaction;

import java.util.Map;
import java.util.UUID;

import weloveclouds.commons.kvstore.models.messages.IKVTransactionMessage;
import weloveclouds.commons.kvstore.models.messages.IKVTransactionMessage.StatusType;
import weloveclouds.commons.kvstore.models.messages.IKVTransferMessage;
import weloveclouds.commons.networking.models.requests.ICallbackRegister;
import weloveclouds.commons.networking.models.requests.IRequestFactory;
import weloveclouds.server.requests.kvserver.transaction.utils.TransactionStatus;
import weloveclouds.server.requests.kvserver.transfer.IKVTransferRequest;

public class TransactionReceiverService
        implements IRequestFactory<IKVTransactionMessage, IKVTransactionRequest> {

    private Map<UUID, TransactionStatus> transactionLog;
    private Map<UUID, IKVTransactionMessage> ongoingTransactions;

    private IRequestFactory<IKVTransferMessage, IKVTransferRequest> simulatedDASBehavior;
    private IRequestFactory<IKVTransferMessage, IKVTransferRequest> realDASBehavior;

    protected TransactionReceiverService(Builder builder) {
        this.transactionLog = builder.transactionLog;
        this.ongoingTransactions = builder.ongoingTransactions;
        this.simulatedDASBehavior = builder.simulatedDASBehavior;
        this.realDASBehavior = builder.realDASBehavior;
    }

    @Override
    public IKVTransactionRequest createRequestFromReceivedMessage(
            IKVTransactionMessage receivedMessage, ICallbackRegister callbackRegister) {
        IKVTransactionRequest request = null;
        StatusType status = receivedMessage.getStatus();

        switch (status) {
            case ABORT:
                request = new AbortRequest.Builder().transactionLog(transactionLog)
                        .ongoingTransactions(ongoingTransactions)
                        .transactionId(receivedMessage.getTransactionId()).build();
                break;
            case INIT:
                request = new InitRequest.Builder().transactionLog(transactionLog)
                        .ongoingTransactions(ongoingTransactions)
                        .transactionId(receivedMessage.getTransactionId()).build();
                break;
            case COMMIT:
                request = new CommitRequest.Builder().transactionLog(transactionLog)
                        .ongoingTransactions(ongoingTransactions)
                        .transactionId(receivedMessage.getTransactionId())
                        .realDASBehavior(realDASBehavior).build();
                break;
            case COMMIT_READY:
                request = new CommitReadyRequest.Builder().transactionLog(transactionLog)
                        .ongoingTransactions(ongoingTransactions)
                        .transactionId(receivedMessage.getTransactionId())
                        .simulatedDASBehavior(simulatedDASBehavior).build();
                break;
            default:
                request = new HelpRequest.Builder().transactionLog(transactionLog)
                        .ongoingTransactions(ongoingTransactions)
                        .transactionId(receivedMessage.getTransactionId()).build();
                break;
        }

        return request;
    }

    public static class Builder {
        private Map<UUID, TransactionStatus> transactionLog;
        private Map<UUID, IKVTransactionMessage> ongoingTransactions;
        private IRequestFactory<IKVTransferMessage, IKVTransferRequest> simulatedDASBehavior;
        private IRequestFactory<IKVTransferMessage, IKVTransferRequest> realDASBehavior;

        public Builder transactionLog(Map<UUID, TransactionStatus> transactionLog) {
            this.transactionLog = transactionLog;
            return this;
        }

        public Builder ongoingTransactions(Map<UUID, IKVTransactionMessage> ongoingTransactions) {
            this.ongoingTransactions = ongoingTransactions;
            return this;
        }

        public Builder simulatedDASBehavior(
                IRequestFactory<IKVTransferMessage, IKVTransferRequest> simulatedDASBehavior) {
            this.simulatedDASBehavior = simulatedDASBehavior;
            return this;
        }

        public Builder realDASBehavior(
                IRequestFactory<IKVTransferMessage, IKVTransferRequest> realDASBehavior) {
            this.simulatedDASBehavior = realDASBehavior;
            return this;
        }

        public TransactionReceiverService build() {
            return new TransactionReceiverService(this);
        }
    }


}
