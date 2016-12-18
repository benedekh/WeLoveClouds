package weloveclouds.server.requests.kvserver.transaction;

import static weloveclouds.server.requests.kvserver.transaction.utils.KVTransactionMessageFactory.createTransactionResponse;

import weloveclouds.commons.kvstore.models.messages.IKVTransactionMessage;
import weloveclouds.commons.kvstore.models.messages.IKVTransactionMessage.StatusType;
import weloveclouds.server.requests.kvserver.transaction.utils.TransactionStatus;

public class InitRequest extends AbstractRequest<InitRequest.Builder> {

    protected InitRequest(Builder builder) {
        super(builder);
    }

    @Override
    public IKVTransactionMessage execute() {
        if (!transactionLog.containsKey(transactionId)) {
            transactionLog.put(transactionId, TransactionStatus.INIT);
        } else {
            TransactionStatus recentStatus = transactionLog.get(transactionId);
            if (recentStatus != TransactionStatus.INIT) {
                // TODO handle if status is HELP_NEEDED
                return createTransactionResponse(transactionId, StatusType.RESPONSE_ABORTED);
            }
        }
        return createTransactionResponse(transactionId, StatusType.RESPONSE_INIT_READY);
    }

    public static class Builder extends AbstractRequest.Builder<Builder> {

        public InitRequest build() {
            return new InitRequest(this);
        }
    }

}
