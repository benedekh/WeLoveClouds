package testing.weloveclouds.server.requests;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import junit.framework.Assert;
import weloveclouds.commons.kvstore.deserialization.KVMessageDeserializer;
import weloveclouds.commons.kvstore.deserialization.exceptions.DeserializationException;
import weloveclouds.commons.kvstore.models.messages.IKVMessage;
import weloveclouds.commons.kvstore.models.messages.IKVMessage.StatusType;
import weloveclouds.commons.kvstore.models.messages.KVMessage;
import weloveclouds.commons.kvstore.serialization.KVMessageSerializer;
import weloveclouds.commons.serialization.IMessageDeserializer;
import weloveclouds.commons.serialization.IMessageSerializer;
import weloveclouds.commons.serialization.models.SerializedMessage;
import weloveclouds.communication.exceptions.ConnectionClosedException;
import weloveclouds.communication.exceptions.UnableToSendContentToServerException;
import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.server.api.KVCommunicationApiFactory;
import weloveclouds.server.api.v2.IKVCommunicationApiV2;
import weloveclouds.server.configuration.models.KVServerPortConstants;


/**
 * Unit tests for validating KVServer, server-side request validation of messages from KVClient.
 * 
 * @author Benedek
 */
public class KVServerHandlingKVClientRequestTests {
    private static final String SERVER_IP_ADDRESS = "localhost";
    private static final int SERVER_KVCLIENT_REQUEST_ACCEPTING_PORT =
            KVServerPortConstants.KVCLIENT_REQUESTS_PORT;

    private IKVCommunicationApiV2 serverCommunication;
    private IMessageDeserializer<IKVMessage, SerializedMessage> kvmessageDeserializer;
    private IMessageSerializer<SerializedMessage, IKVMessage> kvmessageSerializer;

    @Before
    public void setUp() throws Exception {
        ServerConnectionInfo bootstrapConnectionInfo =
                new ServerConnectionInfo.Builder().ipAddress(SERVER_IP_ADDRESS).port(50000).build();
        serverCommunication =
                new KVCommunicationApiFactory().createKVCommunicationApiV2(bootstrapConnectionInfo);
        serverCommunication.connect();

        kvmessageDeserializer = new KVMessageDeserializer();
        kvmessageSerializer = new KVMessageSerializer();
    }

    @After
    public void tearDown() {
        serverCommunication.disconnect();
    }

    @Test
    public void testSendPutMessage() throws UnableToSendContentToServerException,
            ConnectionClosedException, DeserializationException {
        KVMessage message =
                new KVMessage.Builder().status(StatusType.PUT).key("a").value("world").build();
        serverCommunication.send(kvmessageSerializer.serialize(message).getBytes());

        IKVMessage response = kvmessageDeserializer.deserialize(serverCommunication.receive());
        Assert.assertEquals(StatusType.PUT_SUCCESS, response.getStatus());
    }

    @Test
    public void testSendUpdateMessage() throws UnableToSendContentToServerException,
            ConnectionClosedException, DeserializationException {
        KVMessage message =
                new KVMessage.Builder().status(StatusType.PUT).key("a").value("hello").build();
        serverCommunication.send(kvmessageSerializer.serialize(message).getBytes());

        IKVMessage response = kvmessageDeserializer.deserialize(serverCommunication.receive());
        Assert.assertEquals(StatusType.PUT_UPDATE, response.getStatus());
    }

    @Test
    public void testSendDeleteMessage() throws UnableToSendContentToServerException,
            ConnectionClosedException, DeserializationException {
        KVMessage message = new KVMessage.Builder().status(StatusType.PUT).key("a").build();
        serverCommunication.send(kvmessageSerializer.serialize(message).getBytes());

        IKVMessage response = kvmessageDeserializer.deserialize(serverCommunication.receive());
        Assert.assertEquals(StatusType.DELETE_SUCCESS, response.getStatus());
    }

    @Test
    public void testSendGetMessage() throws UnableToSendContentToServerException,
            ConnectionClosedException, DeserializationException {
        KVMessage message = new KVMessage.Builder().status(StatusType.GET).key("a").build();
        serverCommunication.send(kvmessageSerializer.serialize(message).getBytes());

        IKVMessage response = kvmessageDeserializer.deserialize(serverCommunication.receive());
        Assert.assertEquals(StatusType.GET_SUCCESS, response.getStatus());
        Assert.assertEquals("world", response.getValue());
    }

}
