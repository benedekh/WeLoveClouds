package weloveclouds.server.requests.kvserver.transaction;

import static weloveclouds.server.requests.kvserver.transaction.utils.KVTransactionMessageFactory.createTransactionResponse;

import org.apache.log4j.Logger;
import org.joda.time.Duration;

import weloveclouds.commons.kvstore.models.messages.IKVTransactionMessage;
import weloveclouds.commons.kvstore.models.messages.IKVTransactionMessage.StatusType;
import weloveclouds.commons.utils.StringUtils;
import weloveclouds.server.requests.kvserver.transaction.utils.TimedAbortRequest;
import weloveclouds.server.requests.kvserver.transaction.utils.TransactionStatus;

public class InitRequest extends AbstractRequest<InitRequest.Builder> {

    private static final Duration WAIT_BEFORE_ABORT = new Duration(20 * 1000);
    private static final Logger LOGGER = Logger.getLogger(InitRequest.class);

    protected InitRequest(Builder builder) {
        super(builder);
    }

    @Override
    public IKVTransactionMessage execute() {
        LOGGER.debug(StringUtils.join("", "Init phase for transaction (", transactionId,
                ") on reciever side."));

        if (!transactionLog.containsKey(transactionId)) {
            transactionLog.put(transactionId, TransactionStatus.INIT);
            createTimedAbortRequest();
        } else {
            TransactionStatus recentStatus = transactionLog.get(transactionId);
            if (recentStatus != TransactionStatus.INIT) {
                LOGGER.debug(StringUtils.join("", recentStatus, " for transaction (", transactionId,
                        ") on reciever side."));
                return createTransactionResponse(transactionId,
                        StatusType.RESPONSE_GENERATE_NEW_ID);
            }
        }

        LOGGER.debug(StringUtils.join("", "Init_Ready for transaction (", transactionId,
                ") on reciever side."));
        return createTransactionResponse(transactionId, StatusType.RESPONSE_INIT_READY);
    }

    private void createTimedAbortRequest() {
        AbortRequest abortRequest = new AbortRequest.Builder().transactionLog(transactionLog)
                .ongoingTransactions(ongoingTransactions).timedAbortRequests(timedAbortRequests)
                .transactionId(transactionId).build();
        timedAbortRequests.put(transactionId,
                new TimedAbortRequest(abortRequest, WAIT_BEFORE_ABORT));
    }

    public static class Builder extends AbstractRequest.Builder<Builder> {

        public InitRequest build() {
            return new InitRequest(this);
        }
    }

}
