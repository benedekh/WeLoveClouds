package weloveclouds.commons.communication.resend.strategy;

import static weloveclouds.client.utils.CustomStringJoiner.join;

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

public class ExponentialBackoffResendWithResponse extends ExponentialBackoffResend
        implements Observer, IPacketResendWithResponseStrategy {

    private static final Logger LOGGER =
            Logger.getLogger(ExponentialBackoffResendWithResponse.class);

    private byte[] response;
    private Thread receiverThread;

    public ExponentialBackoffResendWithResponse(int maxNumberOfAttempts,
            ICommunicationApi communicationApi, byte[] packet,
            ExponentialBackoffIntervalComputer backoffIntervalComputer) {
        super(maxNumberOfAttempts, communicationApi, packet, backoffIntervalComputer);
        initializePacketReceiver();
    }

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
                } catch (UnableToSendContentToServerException ex) {
                    if (executionStatus != Status.COMPLETED) {
                        receiverThread.interrupt();
                        LOGGER.error(ex);
                        exception = new IOException(ex);
                        executionStatus = Status.FAILED;
                    }
                }
                LOGGER.info(join("", "#",
                        String.valueOf(numberOfAttemptsSoFar)
                                + " resend attempts were made out of #",
                        String.valueOf(maxNumberOfAttempts), " attempts."));
                Thread.sleep(backoffIntervalComputer.computeIntervalFrom(numberOfAttemptsSoFar)
                        .getMillis());
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
        }
    }

    @Override
    public byte[] getResponse() {
        return response;
    }

    @Override
    public void update(Observable sender, Object argument) {
        if (argument instanceof IOException) {
            exception = (IOException) argument;
            executionStatus = Status.FAILED;
        } else if (argument instanceof byte[]) {
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

        private static final Logger LOGGER = Logger.getLogger(PacketReceiver.class);
        private final ICommunicationApi communicationApi;

        public PacketReceiver(ICommunicationApi communicationApi) {
            this.communicationApi = communicationApi;
        }

        @Override
        public void run() {
            try {
                byte[] response = communicationApi.receive();
                setChanged();
                notifyObservers(response);
            } catch (ClientNotConnectedException | ConnectionClosedException e) {
                LOGGER.error(e);
                setChanged();
                notifyObservers(new IOException(e));
            }
        }
    }

}
