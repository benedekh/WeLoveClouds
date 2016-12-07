package weloveclouds.commons.communication;

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import org.apache.log4j.Logger;

import weloveclouds.commons.communication.backoff.IBackoffIntervalComputer;
import weloveclouds.communication.api.ICommunicationApi;
import weloveclouds.communication.exceptions.ClientNotConnectedException;
import weloveclouds.communication.exceptions.ConnectionClosedException;
import weloveclouds.communication.exceptions.UnableToSendContentToServerException;
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

    private byte[] response;

    private Status executionStatus;
    private Thread receiverThread;

    public NetworkPacketResenderWithResponse(int maxNumberOfAttempts,
            ICommunicationApi communicationApi, byte[] packet,
            IBackoffIntervalComputer waitStrategy) {
        super(maxNumberOfAttempts, communicationApi, packet, waitStrategy);
        initializePacketReceiver();
        this.executionStatus = Status.RUNNING;
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
    public byte[] resendPacket() throws IOException {
        while (executionStatus == Status.RUNNING && numberOfAttemptsSoFar < maxNumberOfAttempts) {
            try {
                try {
                    LOGGER.info("Sending packet over the network.");
                    communicationApi.send(packetToBeSent);
                    sleepBeforeNextAttempt();
                    ++numberOfAttemptsSoFar;
                } catch (UnableToSendContentToServerException ex) {
                    sleepBeforeNextAttempt();
                    ++numberOfAttemptsSoFar;
                }
            } catch (InterruptedException ex) {
                receiverThread.interrupt();
                LOGGER.error(ex);
                throw new IOException("Resend unexpectedly stopped.");
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
