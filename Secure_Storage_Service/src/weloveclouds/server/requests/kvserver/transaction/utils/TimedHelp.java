package weloveclouds.server.requests.kvserver.transaction.utils;

import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;
import org.joda.time.Duration;

import weloveclouds.commons.utils.CloseableLock;
import weloveclouds.server.requests.kvserver.transaction.AbortRequest;
import weloveclouds.server.requests.kvserver.transaction.CommitRequest;
import weloveclouds.server.requests.kvserver.transaction.models.ReceivedTransactionContext;
import weloveclouds.server.services.transaction.TransactionServiceFactory;

public class TimedHelp extends Thread implements ITransactionRestorationRequest {

    private static Logger LOGGER = Logger.getLogger(TimedHelp.class);
    private static final Duration WAIT_BEFORE_HELP = new Duration(60 * 1000);

    private ReentrantLock interruptLock;
    private Thread executor;

    private AbortRequest abortRequest;
    private CommitRequest commitRequest;

    private ReceivedTransactionContext transaction;
    private TransactionServiceFactory transactionServiceFactory;

    protected TimedHelp(Builder builder) {
        this.abortRequest = builder.abortRequest;
        this.commitRequest = builder.commitRequest;
        this.transaction = builder.transaction;
        this.transactionServiceFactory = builder.transactionServiceFactory;
        this.interruptLock = new ReentrantLock();
    }

    @Override
    public void schedule() {
        start();
    }

    @Override
    public void unschedule() {
        if (!Thread.currentThread().equals(executor)) {
            try (CloseableLock lock = new CloseableLock(interruptLock)) {
                interrupt();
            }
        }
    }

    @Override
    public void run() {
        try {
            executor = Thread.currentThread();
            LOGGER.debug("Timed help request started.");
            Thread.sleep(WAIT_BEFORE_HELP.getMillis());
            LOGGER.debug("Executing timed help request.");
            try (CloseableLock lock = new CloseableLock(interruptLock)) {
                transactionServiceFactory
                        .create2PCReceiverSideRestorationService(abortRequest, commitRequest)
                        .executeTransactionReferredByIDFor(transaction.getTransactionId(),
                                transaction.getOtherParticipants());
            }
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
