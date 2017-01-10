package weloveclouds.server.requests.kvserver.transaction;

import static weloveclouds.server.requests.kvserver.transaction.utils.KVTransactionMessageFactory.createTransactionResponse;
import static weloveclouds.server.requests.kvserver.transaction.utils.KVTransactionMessageFactory.createUnknownIDTransactionResponse;

import org.apache.log4j.Logger;

import weloveclouds.commons.exceptions.IllegalRequestException;
import weloveclouds.commons.kvstore.models.messages.IKVTransactionMessage;
import weloveclouds.commons.kvstore.models.messages.IKVTransactionMessage.StatusType;
import weloveclouds.commons.utils.StringUtils;
import weloveclouds.server.requests.kvserver.transaction.models.ReceivedTransactionContext;
import weloveclouds.server.requests.kvserver.transaction.models.TransactionStatus;

/**
 * An abort request which terminates the transaction.
 * 
 * @author Benedek
 */
public class AbortRequest extends AbstractRequest<AbortRequest.Builder> {

    private static Logger LOGGER = Logger.getLogger(AbortRequest.class);

    protected AbortRequest(Builder builder) {
        super(builder);
    }

    @Override
    public IKVTransactionMessage execute() {
        LOGGER.debug(StringUtils.join("", "Abort phase for transaction (", transactionId,
                ") on receiver side."));

        ReceivedTransactionContext transaction = transactionLog.get(transactionId);
        TransactionStatus recentStatus = transaction.getTransactionStatus();
        if (!transaction.isCompleted()) {
            transaction.setAborted();
            LOGGER.debug(StringUtils.join("", "Aborted  for transaction (", transactionId,
                    ") on receiver side."));
            return createTransactionResponse(transactionId, StatusType.RESPONSE_ABORTED);
        } else {
            LOGGER.debug(StringUtils.join("", recentStatus, " for transaction (", transactionId,
                    ") on receiver side."));
            return createTransactionResponse(transactionId, recentStatus.getResponseType());
        }
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

    /**
     * Builder pattern for creating a {@link AbortRequest} instance.
     *
     * @author Benedek
     */
    public static class Builder extends AbstractRequest.Builder<Builder> {

        public AbortRequest build() {
            return new AbortRequest(this);
        }
    }
}
