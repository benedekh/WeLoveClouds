package weloveclouds.communication.services.resend;

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import org.apache.log4j.Logger;

import weloveclouds.commons.retryer.IBackoffIntervalComputer;
import weloveclouds.communication.exceptions.ClientNotConnectedException;
import weloveclouds.communication.exceptions.UnableToSendContentToServerException;
import weloveclouds.communication.models.Connection;
import weloveclouds.communication.services.ICommunicationService;
import weloveclouds.communication.services.IConcurrentCommunicationService;
import weloveclouds.ecs.models.tasks.Status;

/**
 * A helper class which can use different wait strategies between resends for sending a packet over
 * the network, but this implementation expects for a response from the recipient.
 * 
 * @author Benedek
 */
public class NetworkPacketResenderWithResponse extends AbstractNetworkPacketResender
        implements Observer {

    private static final Logger LOGGER = Logger.getLogger(NetworkPacketResenderWithResponse.class);

    private volatile byte[] response;
    private Thread senderThread;

    private volatile Status executionStatus;
    private Thread receiverThread;

    public NetworkPacketResenderWithResponse(int maxNumberOfAttempts, byte[] packet,
            IBackoffIntervalComputer waitStrategy) {
        super(maxNumberOfAttempts, packet, waitStrategy);
        this.executionStatus = Status.WAITING;
    }

    @Override
    public byte[] sendWith(ICommunicationService communicationService) throws IOException {
        createPacketReceiver(communicationService);

        while (executionStatus == Status.RUNNING && numberOfAttemptsSoFar < maxNumberOfAttempts) {
            try {
                if (!communicationService.isConnected()) {
                    receiverThread.interrupt();
                    String errorMessage = "Connection is closed, resend stopped.";
                    LOGGER.error(errorMessage);
                    throw new IOException(errorMessage);
                }

                try {
                    LOGGER.info("Sending packet over the network.");
                    communicationService.send(packetToBeSent);
                    sleepBeforeNextAttempt();
                    ++numberOfAttemptsSoFar;
                } catch (UnableToSendContentToServerException ex) {
                    sleepBeforeNextAttempt();
                    ++numberOfAttemptsSoFar;
                }
            } catch (InterruptedException ex) {
                LOGGER.debug(ex);
            }
        }

        if (executionStatus == Status.COMPLETED) {
            return response;
        } else if (numberOfAttemptsSoFar >= maxNumberOfAttempts) {
            receiverThread.interrupt();
            String message = "Max number of retries have been reached.";
            LOGGER.info(message);
            throw new IOException(message);
        } else {
            return new byte[0];
        }
    }

    @Override
    public byte[] sendWith(IConcurrentCommunicationService concurrentCommunicationService,
            Connection<?> connection) throws IOException {
        createPacketReceiver(concurrentCommunicationService, connection);

        while (executionStatus == Status.RUNNING && numberOfAttemptsSoFar < maxNumberOfAttempts) {
            try {
                if (!connection.isConnected()) {
                    receiverThread.interrupt();
                    String errorMessage = "Connection is closed, resend stopped.";
                    LOGGER.error(errorMessage);
                    throw new IOException(errorMessage);
                }

                try {
                    LOGGER.info("Sending packet over the network.");
                    concurrentCommunicationService.send(packetToBeSent, connection);
                    sleepBeforeNextAttempt();
                    ++numberOfAttemptsSoFar;
                } catch (IOException ex) {
                    sleepBeforeNextAttempt();
                    ++numberOfAttemptsSoFar;
                }
            } catch (InterruptedException ex) {
                LOGGER.debug(ex);
            }
        }

        if (executionStatus == Status.COMPLETED) {
            return response;
        } else if (numberOfAttemptsSoFar >= maxNumberOfAttempts) {
            receiverThread.interrupt();
            String message = "Max number of retries have been reached.";
            LOGGER.info(message);
            throw new IOException(message);
        } else {
            return new byte[0];
        }
    }

    @Override
    public void update(Observable sender, Object argument) {
        if (argument instanceof byte[]) {
            response = (byte[]) argument;
            executionStatus = Status.COMPLETED;
            senderThread.interrupt();
        }
    }

    private void createPacketReceiver(ICommunicationService communicationService) {
        PacketReceiver packetReceiver = new PacketReceiver(communicationService);
        packetReceiver.addObserver(this);
        initializePacketReceiverThread(packetReceiver);
    }

    private void createPacketReceiver(
            IConcurrentCommunicationService concurrentCommunicationService,
            Connection<?> connection) {
        PacketReceiver packetReceiver =
                new PacketReceiver(concurrentCommunicationService, connection);
        packetReceiver.addObserver(this);
        initializePacketReceiverThread(packetReceiver);
    }

    private void initializePacketReceiverThread(PacketReceiver packetReceiver) {
        senderThread = Thread.currentThread();
        receiverThread = new Thread(packetReceiver);
        LOGGER.info("Starting packet receiver thread.");
        receiverThread.start();
        while (!receiverThread.isAlive());
        LOGGER.info("Packet receiver thread started.");
        this.executionStatus = Status.RUNNING;
    }

    /**
     * A thread that is used for receiving response packet over the network. As soon as the response
     * arrived, the resend strategy is stopped.
     * 
     * @author Benedek
     */
    private static class PacketReceiver extends Observable implements Runnable {

        private ICommunicationService communicationService;

        private IConcurrentCommunicationService concurrentCommunicationService;
        private Connection<?> connection;

        public PacketReceiver(ICommunicationService communicationService) {
            this.communicationService = communicationService;
        }

        public PacketReceiver(IConcurrentCommunicationService concurrentCommunicationService,
                Connection<?> connection) {
            this.concurrentCommunicationService = concurrentCommunicationService;
            this.connection = connection;
        }

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    byte[] response = null;
                    if (communicationService != null) {
                        response = communicationService.receive();
                    } else if (concurrentCommunicationService != null) {
                        response = concurrentCommunicationService.receiveFrom(connection);
                    }
                    setChanged();
                    notifyObservers(response);
                    return;
                } catch (ClientNotConnectedException | IOException e) {
                    /*
                     * don't log the exceptions, otherwise the log file will be full in case of a
                     * network error
                     */
                }
            }
        }
    }

}
