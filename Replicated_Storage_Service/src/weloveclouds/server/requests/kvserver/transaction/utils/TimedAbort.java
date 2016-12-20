package weloveclouds.server.requests.kvserver.transaction.utils;

import org.apache.log4j.Logger;
import org.joda.time.Duration;

import weloveclouds.commons.utils.StringUtils;
import weloveclouds.server.requests.kvserver.transaction.models.ReceivedTransactionContext;

public class TimedAbort extends Thread {

    private static Logger LOGGER = Logger.getLogger(TimedAbort.class);
    private static final Duration WAIT_BEFORE_ABORT = new Duration(20 * 1000);

    private ReceivedTransactionContext transactionContext;

    public TimedAbort(ReceivedTransactionContext transactionContext) {
        this.transactionContext = transactionContext;
    }

    @Override
    public void run() {
        try {
            LOGGER.debug("Timed abort request started.");
            Thread.sleep(WAIT_BEFORE_ABORT.getMillis());
            LOGGER.debug("Executing timed abort.");
            transactionContext.setAborted();
            LOGGER.debug(StringUtils.join("", "Transaction (",
                    transactionContext.getTransactionId(), ") is set aborted."));
        } catch (InterruptedException ex) {

        } finally {
            LOGGER.debug("Timed abort request stopped.");
        }
    }

}
