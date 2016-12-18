package weloveclouds.server.requests.kvserver.transaction;

import static weloveclouds.server.requests.kvserver.transaction.utils.KVTransactionMessageFactory.createTransactionResponse;

import weloveclouds.commons.kvstore.models.messages.IKVTransactionMessage;
import weloveclouds.commons.kvstore.models.messages.IKVTransactionMessage.StatusType;
import weloveclouds.server.requests.kvserver.transaction.utils.TransactionStatus;

public class AbortRequest extends AbstractRequest<AbortRequest.Builder> {

    protected AbortRequest(Builder builder) {
        super(builder);
    }

    @Override
    public IKVTransactionMessage execute() {
        // TODO stop thread which waits before sending HELP
        ongoingTransactions.remove(transactionId);
        transactionLog.put(transactionId, TransactionStatus.ABORTED);
        return createTransactionResponse(transactionId, StatusType.RESPONSE_ABORTED);
    }

    public static class Builder extends AbstractRequest.Builder<Builder> {

        public AbortRequest build() {
            return new AbortRequest(this);
        }
    }
}
