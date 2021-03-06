package testing.weloveclouds.server.requests.validation;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import junit.framework.Assert;
import weloveclouds.communication.CommunicationApiFactory;
import weloveclouds.server.api.v2.IKVCommunicationApiV2;
import weloveclouds.communication.exceptions.ConnectionClosedException;
import weloveclouds.communication.exceptions.UnableToSendContentToServerException;
import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.commons.kvstore.deserialization.IMessageDeserializer;
import weloveclouds.commons.kvstore.deserialization.KVTransferMessageDeserializer;
import weloveclouds.commons.kvstore.models.messages.IKVTransferMessage.StatusType;
import weloveclouds.commons.kvstore.models.messages.KVTransferMessage;
import weloveclouds.commons.kvstore.serialization.IMessageSerializer;
import weloveclouds.commons.kvstore.serialization.KVTransferMessageSerializer;
import weloveclouds.commons.kvstore.deserialization.exceptions.DeserializationException;
import weloveclouds.commons.kvstore.serialization.models.SerializedMessage;

/**
 * Unit tests for validating KVServer, server-side request validation of messages from another KVServer.
 * 
 * @author Benedek
 */
public class KVServerRequestFromKVServerValidationTests {

    private static final String SERVER_IP_ADDRESS = "localhost";
    private static final int SERVER_KVSERVER_REQUEST_ACCEPTING_PORT = 50001;

    IKVCommunicationApiV2 serverCommunication;
    IMessageDeserializer<KVTransferMessage, SerializedMessage> kvTransferMessageDeserializer;
    IMessageSerializer<SerializedMessage, KVTransferMessage> kvTransferMessageSerializer;

    @Before
    public void init() throws Exception {
        ServerConnectionInfo bootstrapConnectionInfo = new ServerConnectionInfo.Builder()
                .ipAddress(SERVER_IP_ADDRESS).port(SERVER_KVSERVER_REQUEST_ACCEPTING_PORT).build();
        serverCommunication =
                new CommunicationApiFactory().createKVCommunicationApiV2(bootstrapConnectionInfo);
        serverCommunication.connect();

        kvTransferMessageDeserializer = new KVTransferMessageDeserializer();
        kvTransferMessageSerializer = new KVTransferMessageSerializer();
    }

    @After
    public void tearDown() {
        serverCommunication.disconnect();
    }

    @Test
    public void testSendTransferMessageWithoutMovableStorageUnitsInTheMessage()
            throws UnableToSendContentToServerException, ConnectionClosedException,
            DeserializationException {
        KVTransferMessage message = new KVTransferMessage.Builder().status(StatusType.TRANSFER)
                .storageUnits(null).build();
        serverCommunication.send(kvTransferMessageSerializer.serialize(message).getBytes());

        KVTransferMessage response =
                kvTransferMessageDeserializer.deserialize(serverCommunication.receive());
        Assert.assertEquals(StatusType.TRANSFER_ERROR, response.getStatus());
    }


}
