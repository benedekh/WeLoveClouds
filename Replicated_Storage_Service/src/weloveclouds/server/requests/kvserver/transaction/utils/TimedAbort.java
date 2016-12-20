package weloveclouds.server.requests.kvserver.transaction.utils;

import org.apache.log4j.Logger;
import org.joda.time.Duration;

import weloveclouds.server.requests.kvserver.transaction.AbortRequest;

public class TimedAbort extends Thread {

    private static Logger LOGGER = Logger.getLogger(TimedAbort.class);
    private static final Duration WAIT_BEFORE_ABORT = new Duration(20 * 1000);

    private AbortRequest abortRequest;

    public TimedAbort(AbortRequest abortRequest) {
        this.abortRequest = abortRequest;
    }

    @Override
    public void run() {
        try {
            LOGGER.debug("Timed abort request started.");
            Thread.sleep(WAIT_BEFORE_ABORT.getMillis());
            LOGGER.debug("Executing timed abort request.");
            abortRequest.execute();
        } catch (InterruptedException ex) {

        } finally {
            LOGGER.debug("Timed abort request stopped.");
        }
    }

}
