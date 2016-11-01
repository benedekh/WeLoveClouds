package weloveclouds.communication.api.v1;

import java.net.UnknownHostException;

import org.apache.log4j.Logger;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.communication.SocketFactory;
import weloveclouds.communication.api.ICommunicationApi;
import weloveclouds.communication.exceptions.ClientNotConnectedException;
import weloveclouds.communication.exceptions.ConnectionClosedException;
import weloveclouds.communication.exceptions.UnableToConnectException;
import weloveclouds.communication.exceptions.UnableToDisconnectException;
import weloveclouds.communication.exceptions.UnableToSendContentToServerException;
import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.communication.services.CommunicationService;
import weloveclouds.kvstore.IKVMessage;
import weloveclouds.kvstore.IKVMessage.StatusType;
import weloveclouds.kvstore.KVMessage;
import weloveclouds.kvstore.serialization.IMessageDeserializer;
import weloveclouds.kvstore.serialization.IMessageSerializer;
import weloveclouds.kvstore.serialization.SerializedKVMessage;

public class KVCommunicationApiV1 implements IKVCommunicationApi {

    private static final double VERSION = 1.5;

    private ServerConnectionInfo remoteServer;
    private ICommunicationApi serverCommunication;
    private IMessageSerializer<SerializedKVMessage, KVMessage> messageSerializer;
    private IMessageDeserializer<KVMessage, SerializedKVMessage> messageDeserializer;
    private Logger logger;

    /**
     * Creates a new communication instance which connects to the server at the referred addres and
     * port. This constructor is used mainly for testing purposes.
     */
    public KVCommunicationApiV1(String address, int port) {
        this.serverCommunication =
                new CommunicationApiV1(new CommunicationService(new SocketFactory()));
        this.logger = Logger.getLogger(getClass());

        try {
            this.remoteServer = new ServerConnectionInfo.ServerConnectionInfoBuilder()
                    .ipAddress(address).port(port).build();
        } catch (UnknownHostException ex) {
            logger.error(ex.getMessage());
        }
    }

    public KVCommunicationApiV1(ICommunicationApi communicationApi, IMessageSerializer
            messageSerializer, IMessageDeserializer messageDeserializer) {
        this.serverCommunication = communicationApi;
        this.messageSerializer = messageSerializer;
        this.messageDeserializer = messageDeserializer;
        this.logger = Logger.getLogger(getClass());
    }

    @Override
    public void connect() throws Exception {
        serverCommunication.connectTo(remoteServer);
    }

    @Override
    public void disconnect() {
        try {
            serverCommunication.disconnect();
        } catch (UnableToDisconnectException ex) {
            logger.error(ex.getMessage());
        }
    }

    @Override
    public IKVMessage put(String key, String value) throws Exception {
        sendMessage(StatusType.PUT, key, value);
        return receiveMessage();
    }

    @Override
    public IKVMessage get(String key) throws Exception {
        sendMessage(StatusType.GET, key, null);
        return receiveMessage();
    }

    @Override
    public double getVersion() {
        return VERSION;
    }

    @Override
    public boolean isConnected() {
        return serverCommunication.isConnected();
    }

    @Override
    public void connectTo(ServerConnectionInfo remoteServer) throws UnableToConnectException {
        this.remoteServer = remoteServer;

        try {
            connect();
        } catch (Exception ex) {
            logger.error(ex);
            throw new UnableToConnectException(ex.getMessage());
        }
    }

    @Override
    public void send(byte[] content) throws UnableToSendContentToServerException {
        serverCommunication.send(content);
    }

    @Override
    public byte[] receive() throws ClientNotConnectedException, ConnectionClosedException {
        return serverCommunication.receive();
    }

    private void sendMessage(StatusType messageType, String key, String value)
            throws UnableToSendContentToServerException {
        KVMessage message =
                new KVMessage.KVMessageBuilder().status(messageType).key(key).value(value).build();
        byte[] rawMessage = messageSerializer.serialize(message).getBytes();
        send(rawMessage);
        logger.debug(CustomStringJoiner.join(" ", message.toString(), "is sent."));
    }

    private IKVMessage receiveMessage() throws Exception {
        KVMessage response = messageDeserializer.deserialize(receive());
        logger.debug(CustomStringJoiner.join(" ", response.toString(), "is received."));
        return response;
    }

}
