package weloveclouds.server.requests.kvserver.transaction;

import java.util.Map;
import java.util.UUID;

import weloveclouds.commons.kvstore.models.messages.IKVTransactionMessage;
import weloveclouds.commons.kvstore.models.messages.IKVTransactionMessage.StatusType;
import weloveclouds.commons.networking.models.requests.ICallbackRegister;
import weloveclouds.commons.networking.models.requests.IRequestFactory;
import weloveclouds.server.requests.kvserver.transaction.utils.TransactionStatus;

public class TransactionReceiverService
        implements IRequestFactory<IKVTransactionMessage, IKVTransactionRequest> {

    private Map<UUID, TransactionStatus> transactionLog;
    private Map<UUID, IKVTransactionMessage> ongoingTransactions;

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
                        .transactionId(receivedMessage.getTransactionId()).build();
                break;
            case COMMIT_READY:
                request = new CommitReadyRequest.Builder().transactionLog(transactionLog)
                        .ongoingTransactions(ongoingTransactions)
                        .transactionId(receivedMessage.getTransactionId()).build();
                break;
            default:
                request = new HelpRequest.Builder().transactionLog(transactionLog)
                        .ongoingTransactions(ongoingTransactions)
                        .transactionId(receivedMessage.getTransactionId()).build();
                break;
        }

        return request;
    }


}
