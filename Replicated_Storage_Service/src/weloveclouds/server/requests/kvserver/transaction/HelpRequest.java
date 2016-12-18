package weloveclouds.server.requests.kvserver.transaction;

import static weloveclouds.server.requests.kvserver.transaction.utils.KVTransactionMessageFactory.createTransactionResponse;
import static weloveclouds.server.requests.kvserver.transaction.utils.KVTransactionMessageFactory.createUnknownIDTransactionResponse;

import weloveclouds.commons.kvstore.models.messages.IKVTransactionMessage;
import weloveclouds.server.requests.kvserver.transaction.utils.TransactionStatus;

public class HelpRequest extends AbstractRequest<HelpRequest.Builder> {

    protected HelpRequest(Builder builder) {
        super(builder);
    }

    @Override
    public IKVTransactionMessage execute() {
        TransactionStatus transactionStatus = transactionLog.get(transactionId);
        if (transactionStatus != null) {
            return createTransactionResponse(transactionId, transactionStatus.getResponseType());
        } else {
            return createUnknownIDTransactionResponse(transactionId);
        }
    }

    public static class Builder extends AbstractRequest.Builder<Builder> {

        public HelpRequest build() {
            return new HelpRequest(this);
        }
    }

}
