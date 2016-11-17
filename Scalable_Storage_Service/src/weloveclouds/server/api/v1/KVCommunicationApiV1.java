package weloveclouds.server.api.v1;

import org.apache.log4j.Logger;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.communication.SocketFactory;
import weloveclouds.communication.api.ICommunicationApi;
import weloveclouds.communication.api.v1.CommunicationApiV1;
import weloveclouds.communication.exceptions.ClientNotConnectedException;
import weloveclouds.communication.exceptions.ConnectionClosedException;
import weloveclouds.communication.exceptions.UnableToConnectException;
import weloveclouds.communication.exceptions.UnableToDisconnectException;
import weloveclouds.communication.exceptions.UnableToSendContentToServerException;
import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.communication.services.CommunicationService;
import weloveclouds.kvstore.models.IKVMessage;
import weloveclouds.kvstore.models.IKVMessage.StatusType;
import weloveclouds.kvstore.models.KVMessage;
import weloveclouds.kvstore.serialization.IMessageDeserializer;
import weloveclouds.kvstore.serialization.IMessageSerializer;
import weloveclouds.kvstore.serialization.KVMessageDeserializer;
import weloveclouds.kvstore.serialization.KVMessageSerializer;
import weloveclouds.kvstore.serialization.models.SerializedKVMessage;
import weloveclouds.server.api.IKVCommunicationApi;

/**
 * First version implementation of the Key-value store communication API. Simply forwards the method
 * calls to the {@link ICommunicationApi}.
 *
 * @author Benoit, Benedek
 */
public class KVCommunicationApiV1 implements IKVCommunicationApi {

    private static final double VERSION = 1.5;

    private ServerConnectionInfo remoteServer;
    private ICommunicationApi serverCommunication;
    private IMessageSerializer<SerializedKVMessage, KVMessage> messageSerializer;
    private IMessageDeserializer<KVMessage, SerializedKVMessage> messageDeserializer;
    private Logger logger;

    private String address;
    private int port;

    /**
     * Creates a new communication instance which connects to the server at the referred address and
     * port. This constructor is used mainly for testing purposes.
     */
    public KVCommunicationApiV1(String address, int port) {
        this.serverCommunication =
                new CommunicationApiV1(new CommunicationService(new SocketFactory()));
        this.logger = Logger.getLogger(getClass());

        this.address = address;
        this.port = port;

        this.messageSerializer = new KVMessageSerializer();
        this.messageDeserializer = new KVMessageDeserializer();
    }

    /**
     * @param communicationApi which transfers the messages over the network
     * @param messageSerializer to serialized {@link KVMessage} to byte[].
     * @param messageDeserializer to deserialize {@link KVMessage} from byte[].
     */
    public KVCommunicationApiV1(ICommunicationApi communicationApi,
            IMessageSerializer<SerializedKVMessage, KVMessage> messageSerializer,
            IMessageDeserializer<KVMessage, SerializedKVMessage> messageDeserializer) {
        this.serverCommunication = communicationApi;
        this.messageSerializer = messageSerializer;
        this.messageDeserializer = messageDeserializer;
        this.logger = Logger.getLogger(getClass());
    }

    @Override
    public void connect() throws Exception {
        if (remoteServer == null) {
            // only for test purposes in the testing package
            remoteServer = new ServerConnectionInfo.ServerConnectionInfoBuilder().ipAddress(address)
                    .port(port).build();
        }

        serverCommunication.connectTo(remoteServer);
    }

    @Override
    public void disconnect() {
        try {
            serverCommunication.disconnect();
        } catch (UnableToDisconnectException ex) {
            logger.error(ex);
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

    /**
     * Creates a new {@link KVMessage} from the referred parameters, serializes that and sends over
     * the network to the server.
     * 
     * @param messageType type of the message
     * @param key key field's value in the message
     * @param value value fields's value in the message
     * 
     * @throws UnableToSendContentToServerException if any error occurs
     */
    private void sendMessage(StatusType messageType, String key, String value)
            throws UnableToSendContentToServerException {
        KVMessage message =
                new KVMessage.KVMessageBuilder().status(messageType).key(key).value(value).build();
        byte[] rawMessage = messageSerializer.serialize(message).getBytes();
        send(rawMessage);
        logger.debug(CustomStringJoiner.join(" ", message.toString(), "is sent."));
    }

    /**
     * Receives a byte[] response over the network, and deserializes a {@link IKVMessage} from that.
     * 
     * @throws Exception if any error occurs
     */
    private IKVMessage receiveMessage() throws Exception {
        KVMessage response = messageDeserializer.deserialize(receive());
        logger.debug(CustomStringJoiner.join(" ", response.toString(), "is received."));
        return response;
    }

}
