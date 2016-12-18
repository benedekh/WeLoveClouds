package weloveclouds.server.requests.kvserver.transaction;

import static weloveclouds.server.requests.kvserver.transaction.utils.KVTransactionMessageFactory.createTransactionResponse;
import static weloveclouds.server.requests.kvserver.transaction.utils.KVTransactionMessageFactory.createUnknownIDTransactionResponse;

import org.apache.log4j.Logger;

import weloveclouds.commons.exceptions.IllegalRequestException;
import weloveclouds.commons.kvstore.models.messages.IKVTransactionMessage;
import weloveclouds.commons.utils.StringUtils;
import weloveclouds.server.requests.kvserver.transaction.utils.TransactionStatus;

public class HelpRequest extends AbstractRequest<HelpRequest.Builder> {

    private static Logger LOGGER = Logger.getLogger(HelpRequest.class);

    protected HelpRequest(Builder builder) {
        super(builder);
    }

    @Override
    public IKVTransactionMessage execute() {
        TransactionStatus recentStatus = transactionLog.get(transactionId);
        LOGGER.debug(StringUtils.join("", recentStatus, " for transaction (", transactionId,
                ") on reciever side."));
        return createTransactionResponse(transactionId, recentStatus.getResponseType());
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

        public HelpRequest build() {
            return new HelpRequest(this);
        }
    }

}
