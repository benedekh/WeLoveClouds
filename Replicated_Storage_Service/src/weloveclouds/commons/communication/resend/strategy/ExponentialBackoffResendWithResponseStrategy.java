package weloveclouds.commons.communication.resend.strategy;

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import org.apache.log4j.Logger;

import weloveclouds.commons.communication.backoff.ExponentialBackoffIntervalComputer;
import weloveclouds.communication.api.ICommunicationApi;
import weloveclouds.communication.exceptions.ClientNotConnectedException;
import weloveclouds.communication.exceptions.ConnectionClosedException;
import weloveclouds.communication.exceptions.UnableToSendContentToServerException;
import weloveclouds.ecs.models.tasks.Status;

/**
 * A strategy class which implements the 'well-known' exponential backoff strategy used in TCP, for
 * resending packet over the network a couple of times after each other. <br>
 * <br>
 * This implementation expects for a response byte[] packet. As soon as it arrives the strategy is
 * stopped.
 * 
 * @author Benedek
 */
public class ExponentialBackoffResendWithResponseStrategy
        extends AbstractResendStrategyWithBackoffInterval implements Observer {

    private static final Logger LOGGER =
            Logger.getLogger(ExponentialBackoffResendWithResponseStrategy.class);

    private byte[] response;
    private Thread receiverThread;

    public ExponentialBackoffResendWithResponseStrategy(int maxNumberOfAttempts,
            ICommunicationApi communicationApi, byte[] packet,
            ExponentialBackoffIntervalComputer backoffIntervalComputer) {
        super(maxNumberOfAttempts, communicationApi, packet, backoffIntervalComputer);
        initializePacketReceiver();
    }

    /**
     * Initializes the {@link PacketReceiver}.
     */
    private void initializePacketReceiver() {
        PacketReceiver packetReceiver = new PacketReceiver(communicationApi);
        packetReceiver.addObserver(this);
        receiverThread = new Thread(packetReceiver);
        LOGGER.info("Starting packet receiver thread.");
        receiverThread.start();
        while (!receiverThread.isAlive());
        LOGGER.info("Packet receiver thread started.");
    }

    @Override
    public void tryAgain() {
        try {
            if (executionStatus == Status.RUNNING && numberOfAttemptsSoFar < maxNumberOfAttempts) {
                try {
                    LOGGER.info("Sending packet over the network.");
                    communicationApi.send(packetToBeSent);
                    sleepBeforeNextAttempt();
                } catch (UnableToSendContentToServerException ex) {
                    sleepBeforeNextAttempt();
                }
            } else if (numberOfAttemptsSoFar >= maxNumberOfAttempts) {
                if (executionStatus != Status.COMPLETED) {
                    receiverThread.interrupt();
                    String message = "Max number of retries have been reached.";
                    LOGGER.info(message);
                    exception = new IOException(message);
                    executionStatus = Status.FAILED;
                }
            }
        } catch (InterruptedException ex) {
            LOGGER.error(ex);
            exception = new IOException("Resend unexpectedly stopped.");
            executionStatus = Status.FAILED;
        }
    }

    @Override
    public byte[] getResponse() {
        return response;
    }

    @Override
    public void update(Observable sender, Object argument) {
        if (argument instanceof byte[]) {
            response = (byte[]) argument;
            executionStatus = Status.COMPLETED;
        }
    }

    /**
     * A thread that is used for receiving response packet over the network. As soon as the response
     * arrived, the resend strategy is stopped.
     * 
     * @author Benedek
     */
    private static class PacketReceiver extends Observable implements Runnable {

        private final ICommunicationApi communicationApi;

        public PacketReceiver(ICommunicationApi communicationApi) {
            this.communicationApi = communicationApi;
        }

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    byte[] response = communicationApi.receive();
                    setChanged();
                    notifyObservers(response);
                    return;
                } catch (ClientNotConnectedException | ConnectionClosedException e) {
                    /*
                     * don't log the exceptions, otherwise the log file will be full in case of a
                     * network error
                     */
                }
            }
        }
    }

}
