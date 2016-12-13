package testing.weloveclouds.server.requests.validation;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import junit.framework.Assert;
import weloveclouds.commons.kvstore.deserialization.KVTransferMessageDeserializer;
import weloveclouds.commons.kvstore.deserialization.exceptions.DeserializationException;
import weloveclouds.commons.kvstore.models.KVEntry;
import weloveclouds.commons.kvstore.models.messages.IKVTransferMessage;
import weloveclouds.commons.kvstore.models.messages.IKVTransferMessage.StatusType;
import weloveclouds.commons.kvstore.models.messages.KVTransferMessage;
import weloveclouds.commons.kvstore.serialization.KVTransferMessageSerializer;
import weloveclouds.commons.serialization.IMessageDeserializer;
import weloveclouds.commons.serialization.IMessageSerializer;
import weloveclouds.commons.serialization.models.SerializedMessage;
import weloveclouds.commons.utils.PathUtils;
import weloveclouds.communication.exceptions.ConnectionClosedException;
import weloveclouds.communication.exceptions.UnableToSendContentToServerException;
import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.server.api.KVCommunicationApiFactory;
import weloveclouds.server.api.v2.IKVCommunicationApiV2;
import weloveclouds.server.configuration.models.KVServerPortConstants;
import weloveclouds.server.store.models.MovableStorageUnit;

/**
 * Unit tests for validating KVServer, server-side request validation of messages from another
 * KVServer.
 * 
 * @author Benedek
 */
public class KVServerRequestFromKVServerValidationTests {

    private static final String SERVER_IP_ADDRESS = "localhost";
    private static final int SERVER_KVSERVER_REQUEST_ACCEPTING_PORT =
            KVServerPortConstants.KVSERVER_REQUESTS_PORT;

    private IKVCommunicationApiV2 serverCommunication;
    private IMessageDeserializer<IKVTransferMessage, SerializedMessage> kvTransferMessageDeserializer;
    private IMessageSerializer<SerializedMessage, IKVTransferMessage> kvTransferMessageSerializer;

    @Before
    public void init() throws Exception {
        ServerConnectionInfo bootstrapConnectionInfo = new ServerConnectionInfo.Builder()
                .ipAddress(SERVER_IP_ADDRESS).port(SERVER_KVSERVER_REQUEST_ACCEPTING_PORT).build();
        serverCommunication =
                new KVCommunicationApiFactory().createKVCommunicationApiV2(bootstrapConnectionInfo);
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
        KVTransferMessage message = new KVTransferMessage.Builder()
                .status(StatusType.TRANSFER_ENTRIES).storageUnits(null).build();
        serverCommunication.send(kvTransferMessageSerializer.serialize(message).getBytes());

        IKVTransferMessage response =
                kvTransferMessageDeserializer.deserialize(serverCommunication.receive());
        Assert.assertEquals(StatusType.RESPONSE_ERROR, response.getStatus());
    }

    public void testTransfer() throws UnableToSendContentToServerException,
            ConnectionClosedException, DeserializationException, IOException {
        Map<String, String> keyval1 = new HashMap<>();
        keyval1.put("hello", "world");
        keyval1.put("apple", "juice");
        MovableStorageUnit unit1 = new MovableStorageUnit(keyval1, PathUtils.createDummyPath());

        Map<String, String> keyval2 = new HashMap<>(keyval1);
        keyval2.put("orange", "banana");
        MovableStorageUnit unit2 = new MovableStorageUnit(keyval2, PathUtils.createDummyPath());

        Set<MovableStorageUnit> storageUnits = new HashSet<>(Arrays.asList(unit1, unit2));

        KVTransferMessage message = new KVTransferMessage.Builder()
                .status(StatusType.TRANSFER_ENTRIES).storageUnits(storageUnits).build();
        serverCommunication.send(kvTransferMessageSerializer.serialize(message).getBytes());

        IKVTransferMessage response =
                kvTransferMessageDeserializer.deserialize(serverCommunication.receive());
        Assert.assertEquals(StatusType.RESPONSE_SUCCESS, response.getStatus());
    }

    @Test
    public void testPutEntry() throws UnableToSendContentToServerException,
            ConnectionClosedException, DeserializationException {
        KVEntry entry = new KVEntry("hello", "world");

        KVTransferMessage message = new KVTransferMessage.Builder().status(StatusType.PUT_ENTRY)
                .putableEntry(entry).build();
        serverCommunication.send(kvTransferMessageSerializer.serialize(message).getBytes());

        IKVTransferMessage response =
                kvTransferMessageDeserializer.deserialize(serverCommunication.receive());
        Assert.assertEquals(StatusType.RESPONSE_SUCCESS, response.getStatus());
    }

    @Test
    public void testRemoveEntry() throws UnableToSendContentToServerException,
            ConnectionClosedException, DeserializationException {
        String key = "hello";

        KVTransferMessage message = new KVTransferMessage.Builder().status(StatusType.PUT_ENTRY)
                .removableKey(key).build();
        serverCommunication.send(kvTransferMessageSerializer.serialize(message).getBytes());

        IKVTransferMessage response =
                kvTransferMessageDeserializer.deserialize(serverCommunication.receive());
        Assert.assertEquals(StatusType.RESPONSE_SUCCESS, response.getStatus());

    }

}
