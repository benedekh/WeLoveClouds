package weloveclouds.server.requests.kvserver.transaction;

import static weloveclouds.server.requests.kvserver.transaction.utils.KVTransactionMessageFactory.createTransactionResponse;
import static weloveclouds.server.requests.kvserver.transaction.utils.KVTransactionMessageFactory.createUnknownIDTransactionResponse;

import org.apache.log4j.Logger;
import org.joda.time.Duration;

import weloveclouds.commons.exceptions.IllegalRequestException;
import weloveclouds.commons.kvstore.models.messages.IKVTransactionMessage;
import weloveclouds.commons.kvstore.models.messages.IKVTransactionMessage.StatusType;
import weloveclouds.commons.kvstore.models.messages.IKVTransferMessage;
import weloveclouds.commons.utils.StringUtils;
import weloveclouds.server.requests.kvserver.transaction.models.ReceivedTransactionContext;
import weloveclouds.server.requests.kvserver.transaction.models.TransactionStatus;
import weloveclouds.server.requests.kvserver.transaction.utils.TimedAbortRequest;

public class InitRequest extends AbstractRequest<InitRequest.Builder> {

    private static final Duration WAIT_BEFORE_ABORT = new Duration(20 * 1000);
    private static final Logger LOGGER = Logger.getLogger(InitRequest.class);

    private IKVTransferMessage transferMessage;

    protected InitRequest(Builder builder) {
        super(builder);
        this.transferMessage = builder.transferMessage;
    }

    @Override
    public IKVTransactionMessage execute() {
        LOGGER.debug(StringUtils.join("", "Init phase for transaction (", transactionId,
                ") on receiver side."));

        if (!transactionLog.containsKey(transactionId)) {
            synchronized (transactionLog) {
                if (!transactionLog.containsKey(transactionId)) {
                    ReceivedTransactionContext transaction = createTransactionContext();
                    transactionLog.put(transactionId, transaction);
                    transaction.scheduleAbortRequest();
                }
            }
        } else {
            TransactionStatus recentStatus =
                    transactionLog.get(transactionId).getTransactionStatus();
            if (recentStatus != TransactionStatus.INIT) {
                LOGGER.debug(StringUtils.join("", recentStatus, " for transaction (", transactionId,
                        ") on receiver side."));
                return createTransactionResponse(transactionId,
                        StatusType.RESPONSE_GENERATE_NEW_ID);
            }
        }

        LOGGER.debug(StringUtils.join("", "Init_Ready for transaction (", transactionId,
                ") on receiver side."));
        return createTransactionResponse(transactionId, StatusType.RESPONSE_INIT_READY);
    }

    @Override
    public IKVTransactionRequest validate() throws IllegalArgumentException {
        super.validate();
        if (transferMessage == null) {
            LOGGER.error("Transfer message is null.");
            throw new IllegalRequestException(createUnknownIDTransactionResponse(null));
        }
        return this;
    }

    private ReceivedTransactionContext createTransactionContext() {
        AbortRequest abortRequest = new AbortRequest.Builder().transactionLog(transactionLog)
                .transactionId(transactionId).build();
        TimedAbortRequest timedAbort = new TimedAbortRequest(abortRequest, WAIT_BEFORE_ABORT);
        return new ReceivedTransactionContext.Builder().transactionStatus(TransactionStatus.INIT)
                .transferMessage(transferMessage).timedAbortRequest(timedAbort).build();
    }

    public static class Builder extends AbstractRequest.Builder<Builder> {

        private IKVTransferMessage transferMessage;

        public Builder transferMessage(IKVTransferMessage transferMessage) {
            this.transferMessage = transferMessage;
            return this;
        }

        public InitRequest build() {
            return new InitRequest(this);
        }
    }

}
