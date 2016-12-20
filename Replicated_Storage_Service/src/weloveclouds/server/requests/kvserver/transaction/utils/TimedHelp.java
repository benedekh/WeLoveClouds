package weloveclouds.server.requests.kvserver.transaction.utils;

import org.apache.log4j.Logger;
import org.joda.time.Duration;

import weloveclouds.server.requests.kvserver.transaction.AbortRequest;
import weloveclouds.server.requests.kvserver.transaction.CommitRequest;
import weloveclouds.server.requests.kvserver.transaction.models.ReceivedTransactionContext;
import weloveclouds.server.services.transaction.TransactionServiceFactory;

public class TimedHelp extends Thread {

    private static Logger LOGGER = Logger.getLogger(TimedHelp.class);
    private static final Duration WAIT_BEFORE_HELP = new Duration(2 * 1000);

    private AbortRequest abortRequest;
    private CommitRequest commitRequest;

    private ReceivedTransactionContext transaction;
    private TransactionServiceFactory transactionServiceFactory;

    protected TimedHelp(Builder builder) {
        this.abortRequest = builder.abortRequest;
        this.commitRequest = builder.commitRequest;
        this.transaction = builder.transaction;
        this.transactionServiceFactory = builder.transactionServiceFactory;
    }

    @Override
    public void run() {
        try {
            LOGGER.debug("Timed help request started.");
            Thread.sleep(WAIT_BEFORE_HELP.getMillis());
            LOGGER.debug("Executing timed help request.");
            transactionServiceFactory
                    .create2PCReceiverSideRestorationService(abortRequest, commitRequest)
                    .executeEmptyTransactionsFor(transaction.getTransactionId(),
                            transaction.getOtherParticipants());
        } catch (InterruptedException ex) {

        } finally {
            LOGGER.debug("Timed help request stopped.");
        }
    }

    public static class Builder {
        private ReceivedTransactionContext transaction;
        private AbortRequest abortRequest;
        private CommitRequest commitRequest;
        private TransactionServiceFactory transactionServiceFactory;

        public Builder transaction(ReceivedTransactionContext transaction) {
            this.transaction = transaction;
            return this;
        }

        public Builder abortRequest(AbortRequest abortRequest) {
            this.abortRequest = abortRequest;
            return this;
        }

        public Builder commitRequest(CommitRequest commitRequest) {
            this.commitRequest = commitRequest;
            return this;
        }

        public Builder transactionServiceFactory(
                TransactionServiceFactory transactionServiceFactory) {
            this.transactionServiceFactory = transactionServiceFactory;
            return this;
        }

        public TimedHelp build() {
            return new TimedHelp(this);
        }

    }
}
