package testing.weloveclouds.server.requests.validation;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import junit.framework.Assert;
import junit.framework.TestCase;
import weloveclouds.communication.CommunicationApiFactory;
import weloveclouds.communication.exceptions.ConnectionClosedException;
import weloveclouds.communication.exceptions.UnableToSendContentToServerException;
import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.commons.kvstore.deserialization.IMessageDeserializer;
import weloveclouds.commons.kvstore.deserialization.KVMessageDeserializer;
import weloveclouds.commons.kvstore.models.messages.IKVMessage.StatusType;
import weloveclouds.commons.kvstore.models.messages.KVMessage;
import weloveclouds.commons.kvstore.serialization.IMessageSerializer;
import weloveclouds.commons.kvstore.serialization.KVMessageSerializer;
import weloveclouds.commons.kvstore.deserialization.exceptions.DeserializationException;
import weloveclouds.commons.kvstore.serialization.models.SerializedMessage;
import weloveclouds.server.api.v2.IKVCommunicationApiV2;

/**
 * Unit tests for validating KVServer, server-side request validation of messages from KVClient.
 * 
 * @author Benedek
 */
public class KVServerRequestFromKVClientValidationTests extends TestCase {

    private static final String SERVER_IP_ADDRESS = "localhost";
    private static final int SERVER_KVCLIENT_REQUEST_ACCEPTING_PORT = 50000;

    IKVCommunicationApiV2 serverCommunication;
    IMessageDeserializer<KVMessage, SerializedMessage> kvmessageDeserializer;
    IMessageSerializer<SerializedMessage, KVMessage> kvmessageSerializer;

    @Before
    public void setUp() throws Exception {
        ServerConnectionInfo bootstrapConnectionInfo = new ServerConnectionInfo.Builder()
                .ipAddress(SERVER_IP_ADDRESS).port(SERVER_KVCLIENT_REQUEST_ACCEPTING_PORT).build();
        serverCommunication =
                new CommunicationApiFactory().createKVCommunicationApiV2(bootstrapConnectionInfo);
        serverCommunication.connect();

        kvmessageDeserializer = new KVMessageDeserializer();
        kvmessageSerializer = new KVMessageSerializer();
    }

    @After
    public void tearDown() {
        serverCommunication.disconnect();
    }

    @Test
    public void testSendPutMessageWithoutKey() throws UnableToSendContentToServerException,
            ConnectionClosedException, DeserializationException {
        KVMessage message =
                new KVMessage.Builder().status(StatusType.PUT).key(null).value("default").build();
        serverCommunication.send(kvmessageSerializer.serialize(message).getBytes());

        KVMessage response = kvmessageDeserializer.deserialize(serverCommunication.receive());
        Assert.assertEquals(StatusType.PUT_ERROR, response.getStatus());
    }

    @Test
    public void testSendDeleteMessageWithoutKey() throws UnableToSendContentToServerException,
            ConnectionClosedException, DeserializationException {
        KVMessage message = new KVMessage.Builder().status(StatusType.PUT).key(null).build();
        serverCommunication.send(kvmessageSerializer.serialize(message).getBytes());

        KVMessage response = kvmessageDeserializer.deserialize(serverCommunication.receive());
        Assert.assertEquals(StatusType.DELETE_ERROR, response.getStatus());
    }

    @Test
    public void testSendGetMessageWithoutKey() throws UnableToSendContentToServerException,
            ConnectionClosedException, DeserializationException {
        KVMessage message = new KVMessage.Builder().status(StatusType.GET).key(null).build();
        serverCommunication.send(kvmessageSerializer.serialize(message).getBytes());

        KVMessage response = kvmessageDeserializer.deserialize(serverCommunication.receive());
        Assert.assertEquals(StatusType.GET_ERROR, response.getStatus());
    }

}
