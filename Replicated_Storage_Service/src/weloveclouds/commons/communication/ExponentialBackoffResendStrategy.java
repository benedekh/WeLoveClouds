package weloveclouds.commons.communication;

import static weloveclouds.client.utils.CustomStringJoiner.join;

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;

import org.apache.log4j.Logger;

import weloveclouds.communication.api.ICommunicationApi;
import weloveclouds.communication.exceptions.ClientNotConnectedException;
import weloveclouds.communication.exceptions.ConnectionClosedException;
import weloveclouds.communication.exceptions.UnableToSendContentToServerException;
import weloveclouds.ecs.models.tasks.Status;

/**
 * A strategy class which implements the 'well-known' exponential backoff strategy used in TCP, for
 * resending packet over the network a couple of times after each other.
 * 
 * @author Benedek
 */
public class ExponentialBackoffResendStrategy implements IPacketResendStrategy, Observer {

    private static final Logger LOGGER = Logger.getLogger(ExponentialBackoffResendStrategy.class);
    private static final int MIN_INTERVAL_IN_MILLISECONDS = 300;

    private int maxNumberOfAttempts;
    private ICommunicationApi communicationApi;
    private byte[] packetToBeSent;

    private int numberOfAttemptsSoFar;
    private Thread receiver;
    private Random numberGenerator;

    private byte[] response;
    private IOException exception;
    private Status executionStatus;

    public ExponentialBackoffResendStrategy() {
        this.executionStatus = Status.WAITING;
        this.numberGenerator = new Random();
    }

    @Override
    public void initialize(int attemptNumber, ICommunicationApi communicationApi, byte[] packet) {
        this.maxNumberOfAttempts = attemptNumber;
        this.communicationApi = communicationApi;
        packetToBeSent = packet;

        PacketReceiver packetReceiver = new PacketReceiver(communicationApi);
        packetReceiver.addObserver(this);
        receiver = new Thread(packetReceiver);
        LOGGER.info("Starting packet receiver thread.");
        receiver.start();
        while (!receiver.isAlive());
        LOGGER.info("Packet receiver thread started.");
        executionStatus = Status.RUNNING;
    }

    @Override
    public void tryAgain() {
        try {
            if (executionStatus == Status.RUNNING && numberOfAttemptsSoFar <= maxNumberOfAttempts) {
                try {
                    LOGGER.info("Sending packet over the network.");
                    communicationApi.send(packetToBeSent);
                } catch (UnableToSendContentToServerException ex) {
                    if (executionStatus != Status.COMPLETED) {
                        receiver.interrupt();
                        LOGGER.error(ex);
                        exception = new IOException(ex);
                        executionStatus = Status.FAILED;
                    }
                }

                LOGGER.info(join("", "#",
                        String.valueOf(numberOfAttemptsSoFar)
                                + " resend attempts were made out of #",
                        String.valueOf(maxNumberOfAttempts), " attempts."));

                int powerOfTwo = (int) Math.round(Math.max(2, Math.pow(2, numberOfAttemptsSoFar)));
                LOGGER.info(join("", "Power of two: ", String.valueOf(powerOfTwo)));

                int drawnFactor = Math.max(1, numberGenerator.nextInt(powerOfTwo - 1));
                LOGGER.info(join("", "Drawn multiplication factor: ", String.valueOf(powerOfTwo)));

                int sleepTime = drawnFactor * MIN_INTERVAL_IN_MILLISECONDS;
                LOGGER.info(join("", "Sleep time in milliseconds before next resend: ",
                        String.valueOf(sleepTime)));

                Thread.sleep(sleepTime);
            } else if (numberOfAttemptsSoFar > maxNumberOfAttempts) {
                receiver.interrupt();
                String message = "Max number of retries have been reached.";
                LOGGER.info(message);
                exception = new IOException(message);
                executionStatus = Status.FAILED;
            }
        } catch (InterruptedException ex) {
            LOGGER.error(ex);
        }
    }

    @Override
    public Status getExecutionStatus() {
        return executionStatus;
    }

    @Override
    public IOException getException() {
        return exception;
    }

    @Override
    public byte[] getResponse() {
        return response;
    }


    @Override
    public void incrementNumberOfAttemptsByOne() {
        numberOfAttemptsSoFar++;
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
