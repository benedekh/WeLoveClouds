package weloveclouds.server.requests.kvserver.transaction.utils;

import org.joda.time.Duration;

import weloveclouds.server.requests.kvserver.transaction.AbortRequest;

public class TimedAbortRequest extends Thread {

    private AbortRequest abortRequest;
    private Duration waitBeforeAbort;

    public TimedAbortRequest(AbortRequest abortRequest, Duration waitBeforeAbort) {
        this.abortRequest = abortRequest;
        this.waitBeforeAbort = waitBeforeAbort;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(waitBeforeAbort.getMillis());
            abortRequest.execute();
        } catch (InterruptedException ex) {

        }
    }

}
