package weloveclouds.server.api.v1;

import java.io.IOException;

import org.apache.log4j.Logger;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.commons.communication.NetworkPacketResenderFactory;
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
import weloveclouds.kvstore.deserialization.IMessageDeserializer;
import weloveclouds.kvstore.deserialization.KVMessageDeserializer;
import weloveclouds.kvstore.models.messages.IKVMessage;
import weloveclouds.kvstore.models.messages.IKVMessage.StatusType;
import weloveclouds.kvstore.models.messages.KVMessage;
import weloveclouds.kvstore.serialization.IMessageSerializer;
import weloveclouds.kvstore.serialization.KVMessageSerializer;
import weloveclouds.kvstore.serialization.models.SerializedMessage;
import weloveclouds.server.api.IKVCommunicationApi;

/**
 * First version implementation of the Key-value store communication API. Simply forwards the method
 * calls to the {@link ICommunicationApi}.
 *
 * @author Benoit, Benedek
 */
public class KVCommunicationApiV1 implements IKVCommunicationApi {

    private static final double VERSION = 1.5;
    private static final Logger LOGGER = Logger.getLogger(KVCommunicationApiV1.class);

    private ServerConnectionInfo remoteServer;
    private ICommunicationApi serverCommunication;
    private IMessageSerializer<SerializedMessage, KVMessage> messageSerializer;
    private IMessageDeserializer<KVMessage, SerializedMessage> messageDeserializer;
    private String address;
    private int port;

    /**
     * Creates a new communication instance which connects to the server at the referred address and
     * port. This constructor is used mainly for testing purposes.
     */
    public KVCommunicationApiV1(String address, int port) {
        this.serverCommunication = new CommunicationApiV1(
                new CommunicationService(new SocketFactory()), new NetworkPacketResenderFactory());

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
            IMessageSerializer<SerializedMessage, KVMessage> messageSerializer,
            IMessageDeserializer<KVMessage, SerializedMessage> messageDeserializer) {
        this.serverCommunication = communicationApi;
        this.messageSerializer = messageSerializer;
        this.messageDeserializer = messageDeserializer;
    }

    @Override
    public void connect() throws Exception {
        if (remoteServer == null) {
            remoteServer = new ServerConnectionInfo.Builder().ipAddress(address).port(port).build();
        }

        serverCommunication.connectTo(remoteServer);
    }

    @Override
    public void disconnect() {
        try {
            serverCommunication.disconnect();
        } catch (UnableToDisconnectException ex) {
            LOGGER.error(ex);
        }
    }

    @Override
    public IKVMessage put(String key, String value) throws Exception {
        byte[] responsePacket = sendMessage(StatusType.PUT, key, value);
        return convertToKVMessage(responsePacket);
    }

    @Override
    public IKVMessage get(String key) throws Exception {
        byte[] responsePacket = sendMessage(StatusType.GET, key, null);
        return convertToKVMessage(responsePacket);
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
            LOGGER.error(ex);
            throw new UnableToConnectException(ex.getMessage());
        }
    }

    @Override
    public void send(byte[] content) throws UnableToSendContentToServerException {
        serverCommunication.send(content);
    }

    @Override
    public byte[] sendAndExpectForResponse(byte[] content) throws IOException {
        return serverCommunication.sendAndExpectForResponse(content);
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
     * 
     * @return the response received for that message
     */
    private byte[] sendMessage(StatusType messageType, String key, String value)
            throws UnableToSendContentToServerException {
        try {
            KVMessage message =
                    new KVMessage.Builder().status(messageType).key(key).value(value).build();
            byte[] rawMessage = messageSerializer.serialize(message).getBytes();
            return sendAndExpectForResponse(rawMessage);
        } catch (IOException ex) {
            LOGGER.error(ex);
            throw new UnableToSendContentToServerException(ex.getMessage());
        }
    }

    /**
     * Deserializes a {@link IKVMessage} from the parameter byte[] if it is possible.
     * 
     * @throws Exception if any error occurs
     */
    private IKVMessage convertToKVMessage(byte[] packet) throws Exception {
        KVMessage response = messageDeserializer.deserialize(packet);
        LOGGER.debug(CustomStringJoiner.join(" ", response.toString(), "is received."));
        return response;
    }

}
