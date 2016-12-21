package weloveclouds.server.requests.kvserver.transaction.utils;

import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;
import org.joda.time.Duration;

import weloveclouds.commons.utils.CloseableLock;
import weloveclouds.server.requests.kvserver.transaction.AbortRequest;

public class TimedAbort extends Thread implements ITransactionRestorationRequest {

    private static Logger LOGGER = Logger.getLogger(TimedAbort.class);
    private static final Duration WAIT_BEFORE_ABORT = new Duration(60 * 1000);

    private ReentrantLock interruptLock;
    private Thread executor;

    private AbortRequest abortRequest;

    public TimedAbort(AbortRequest abortRequest) {
        this.abortRequest = abortRequest;
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
            LOGGER.debug("Timed abort request started.");
            Thread.sleep(WAIT_BEFORE_ABORT.getMillis());
            LOGGER.debug("Executing timed abort request.");
            try (CloseableLock lock = new CloseableLock(interruptLock)) {
                abortRequest.execute();
            }
        } catch (InterruptedException ex) {

        } finally {
            LOGGER.debug("Timed abort request stopped.");
        }
    }

}
