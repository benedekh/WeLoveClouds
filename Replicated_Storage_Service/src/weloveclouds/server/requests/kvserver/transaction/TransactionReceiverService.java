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
import weloveclouds.server.requests.kvserver.transaction.utils.TimedAbortRequest;
import weloveclouds.server.requests.kvserver.transaction.utils.TransactionStatus;
import weloveclouds.server.requests.kvserver.transfer.IKVTransferRequest;

public class TransactionReceiverService
        implements IRequestFactory<IKVTransactionMessage, IKVTransactionRequest> {

    private static final Logger LOGGER = Logger.getLogger(TransactionReceiverService.class);

    private Map<UUID, TransactionStatus> transactionLog;
    private Map<UUID, IKVTransferMessage> ongoingTransactions;
    private Map<UUID, TimedAbortRequest> timedAbortRequests;

    private IRequestFactory<IKVTransferMessage, IKVTransferRequest> simulatedDASBehavior;
    private IRequestFactory<IKVTransferMessage, IKVTransferRequest> realDASBehavior;

    protected TransactionReceiverService(Builder builder) {
        this.transactionLog = builder.transactionLog;
        this.ongoingTransactions = builder.ongoingTransactions;
        this.timedAbortRequests = builder.timedAbortRequests;
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
                        .ongoingTransactions(ongoingTransactions)
                        .timedAbortRequests(timedAbortRequests)
                        .transactionId(receivedMessage.getTransactionId()).build();
                break;
            case INIT:
                request = new InitRequest.Builder().transactionLog(transactionLog)
                        .ongoingTransactions(ongoingTransactions)
                        .timedAbortRequests(timedAbortRequests)
                        .transactionId(receivedMessage.getTransactionId())
                        .transferMessage(receivedMessage.getTransferPayload()).build();
                break;
            case COMMIT:
                request = new CommitRequest.Builder().transactionLog(transactionLog)
                        .ongoingTransactions(ongoingTransactions)
                        .timedAbortRequests(timedAbortRequests)
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
        private Map<UUID, IKVTransferMessage> ongoingTransactions;
        private Map<UUID, TimedAbortRequest> timedAbortRequests;
        private IRequestFactory<IKVTransferMessage, IKVTransferRequest> simulatedDASBehavior;
        private IRequestFactory<IKVTransferMessage, IKVTransferRequest> realDASBehavior;

        public Builder transactionLog(Map<UUID, TransactionStatus> transactionLog) {
            this.transactionLog = transactionLog;
            return this;
        }

        public Builder ongoingTransactions(Map<UUID, IKVTransferMessage> ongoingTransactions) {
            this.ongoingTransactions = ongoingTransactions;
            return this;
        }

        public Builder timedAbortRequests(Map<UUID, TimedAbortRequest> timedAbortRequests) {
            this.timedAbortRequests = timedAbortRequests;
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

        public TransactionReceiverService build() {
            return new TransactionReceiverService(this);
        }
    }


}