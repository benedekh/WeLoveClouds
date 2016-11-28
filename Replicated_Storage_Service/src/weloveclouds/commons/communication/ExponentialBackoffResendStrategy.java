package weloveclouds.commons.communication;

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;

import org.apache.log4j.Logger;

import weloveclouds.communication.api.ICommunicationApi;
import weloveclouds.communication.exceptions.ClientNotConnectedException;
import weloveclouds.communication.exceptions.ConnectionClosedException;
import weloveclouds.communication.exceptions.UnableToSendContentToServerException;

public class ExponentialBackoffResendStrategy implements IPacketResendStrategy, Observer {

    private static final Logger LOGGER = Logger.getLogger(ExponentialBackoffResendStrategy.class);

    private int attemptNumber;

    private Thread sender;
    private Thread receiver;
    private Thread resender;

    private byte[] response;
    private IOException exception;

    @Override
    public void configure(int attemptNumber) {
        this.attemptNumber = attemptNumber;
    }

    @Override
    public synchronized byte[] resendPacket(ICommunicationApi communicationApi, byte[] packet)
            throws IOException {
        receiver = new Thread(new PacketReceiver(communicationApi));
        sender = new Thread(new PacketSender(packet, communicationApi));
        resender = new Thread(new PacketResender(attemptNumber, sender));

        LOGGER.debug("Starting sender, receiver and resender threads.");
        receiver.start();
        while (!receiver.isAlive());
        sender.start();
        while (!sender.isAlive());
        resender.start();

        try {
            wait();
        } catch (InterruptedException e) {
            LOGGER.error(e);
            throw new IOException("Packet resend strategy abruptly stopped.");
        }

        if (exception != null) {
            stopThreads();
            throw exception;
        } else if (response != null) {
            stopThreads();
            return response;
        } else {
            stopThreads();
            throw new IOException("Unknown reason why packet resend strategy was waken up.");
        }
    }

    private void stopThreads() {
        sender.interrupt();
        receiver.interrupt();
        resender.interrupt();
    }

    private static class PacketResender extends Observable implements Runnable {

        private static final Logger LOGGER = Logger.getLogger(PacketResender.class);
        private static final int MIN_INTERVAL_IN_MILLISECONDS = 80;

        private int maxNumberOfAttempts;
        private int numberOfAttemptsSoFar;
        private Random numberGenerator;

        private Thread packetSender;

        public PacketResender(int maxNumberOfAttempts, Thread packetSender) {
            this.maxNumberOfAttempts = maxNumberOfAttempts;
            this.numberOfAttemptsSoFar = 0;
            this.numberGenerator = new Random();
        }

        @Override
        public void run() {
            try {
                int sleepTime = 0;
                while (!Thread.currentThread().isInterrupted()
                        && numberOfAttemptsSoFar <= maxNumberOfAttempts) {
                    int powerOfTwo = (int) Math.round(Math.pow(2, numberOfAttemptsSoFar));
                    sleepTime =
                            numberGenerator.nextInt(powerOfTwo - 1) * MIN_INTERVAL_IN_MILLISECONDS;
                    Thread.sleep(sleepTime);
                    synchronized (packetSender) {
                        packetSender.notify();
                    }
                    numberOfAttemptsSoFar++;
                }
                throw new IOException("Max number of retries have been reached.");
            } catch (IOException | InterruptedException e) {
                LOGGER.error(e);
                setChanged();
                notifyObservers(new IOException(e));
            }
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

    private static class PacketSender extends Observable implements Runnable {

        private static final Logger LOGGER = Logger.getLogger(PacketSender.class);

        private final byte[] packet;
        private final ICommunicationApi communicationApi;

        public PacketSender(byte[] packet, ICommunicationApi communicationApi) {
            this.packet = packet;
            this.communicationApi = communicationApi;
        }

        @Override
        public synchronized void run() {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    wait();
                    communicationApi.send(packet);
                }
            } catch (UnableToSendContentToServerException | InterruptedException e) {
                LOGGER.error(e);
                setChanged();
                notifyObservers(new IOException(e));
            }
        }
    }

    @Override
    public synchronized void update(Observable sender, Object argument) {
        if (argument instanceof IOException) {
            exception = (IOException) argument;
            notify();
        } else if (argument instanceof byte[]) {
            response = (byte[]) argument;
            notify();
        }
    }

}
