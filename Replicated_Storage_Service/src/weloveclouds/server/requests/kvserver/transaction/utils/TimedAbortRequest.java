package weloveclouds.server.requests.kvserver.transaction.utils;

import org.apache.log4j.Logger;
import org.joda.time.Duration;

import weloveclouds.server.requests.kvserver.transaction.AbortRequest;

public class TimedAbortRequest extends Thread {

    private static Logger LOGGER = Logger.getLogger(TimedAbortRequest.class);

    private AbortRequest abortRequest;
    private Duration waitBeforeAbort;

    public TimedAbortRequest(AbortRequest abortRequest, Duration waitBeforeAbort) {
        this.abortRequest = abortRequest;
        this.waitBeforeAbort = waitBeforeAbort;
    }

    @Override
    public void run() {
        try {
            LOGGER.debug("Timed abort request started.");
            Thread.sleep(waitBeforeAbort.getMillis());
            LOGGER.debug("Executing timed abort.");
            abortRequest.execute();
        } catch (InterruptedException ex) {

        } finally {
            LOGGER.debug("Timed abort request stopped.");
        }
    }

}
