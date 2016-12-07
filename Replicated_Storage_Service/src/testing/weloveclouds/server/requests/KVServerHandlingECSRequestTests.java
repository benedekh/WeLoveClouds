package testing.weloveclouds.server.requests;

import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashSet;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import junit.framework.Assert;
import weloveclouds.communication.CommunicationApiFactory;
import weloveclouds.communication.exceptions.ConnectionClosedException;
import weloveclouds.communication.exceptions.UnableToSendContentToServerException;
import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.commons.hashing.models.Hash;
import weloveclouds.commons.hashing.models.HashRange;
import weloveclouds.commons.hashing.models.RingMetadata;
import weloveclouds.commons.hashing.models.RingMetadataPart;
import weloveclouds.commons.hashing.utils.HashingUtil;
import weloveclouds.commons.serialization.IMessageDeserializer;
import weloveclouds.kvstore.deserialization.KVAdminMessageDeserializer;
import weloveclouds.kvstore.models.messages.IKVAdminMessage.StatusType;
import weloveclouds.kvstore.models.messages.KVAdminMessage;
import weloveclouds.commons.serialization.IMessageSerializer;
import weloveclouds.kvstore.serialization.KVAdminMessageSerializer;
import weloveclouds.kvstore.serialization.exceptions.DeserializationException;
import weloveclouds.kvstore.serialization.models.SerializedMessage;
import weloveclouds.server.api.KVCommunicationApiFactory;
import weloveclouds.server.api.v2.IKVCommunicationApiV2;
import weloveclouds.server.models.conf.KVServerPortConstants;

public class KVServerHandlingECSRequestTests {

    private static final String SERVER_IP_ADDRESS = "localhost";

    private static final int SERVER1_KVCLIENT_REQUEST_ACCEPTING_PORT =
            KVServerPortConstants.KVCLIENT_REQUESTS_PORT;
    private static final int SERVER2_KVCLIENT_REQUEST_ACCEPTING_PORT = 60000;

    private static final int SERVER1_KVSERVER_REQUEST_ACCEPTING_PORT =
            KVServerPortConstants.KVSERVER_REQUESTS_PORT;
    private static final int SERVER2_KVSERVER_REQUEST_ACCEPTING_PORT = 60001;

    private static final int SERVER1_KVECS_REQUEST_ACCEPTING_PORT =
            KVServerPortConstants.KVECS_REQUESTS_PORT;
    private static final int SERVER2_KVECS_REQUEST_ACCEPTING_PORT = 60002;

    private IKVCommunicationApiV2 serverCommunication;
    private IMessageDeserializer<KVAdminMessage, SerializedMessage> kvAdminMessageDeserializer;
    private IMessageSerializer<SerializedMessage, KVAdminMessage> kvAdminMessageSerializer;

    @Before
    public void init() throws Exception {
        ServerConnectionInfo bootstrapConnectionInfo = new ServerConnectionInfo.Builder()
                .ipAddress(SERVER_IP_ADDRESS).port(SERVER1_KVECS_REQUEST_ACCEPTING_PORT).build();
        serverCommunication =
                new KVCommunicationApiFactory().createKVCommunicationApiV2(bootstrapConnectionInfo);
        serverCommunication.connect();

        kvAdminMessageDeserializer = new KVAdminMessageDeserializer();
        kvAdminMessageSerializer = new KVAdminMessageSerializer();
    }

    @After
    public void tearDown() {
        serverCommunication.disconnect();
    }

    @Test
    public void testStart() throws UnableToSendContentToServerException, ConnectionClosedException,
            DeserializationException {
        KVAdminMessage adminMessage = new KVAdminMessage.Builder().status(StatusType.START).build();
        serverCommunication.send(kvAdminMessageSerializer.serialize(adminMessage).getBytes());
        KVAdminMessage response =
                kvAdminMessageDeserializer.deserialize(serverCommunication.receive());

        Assert.assertEquals(StatusType.RESPONSE_SUCCESS, response.getStatus());
    }

    @Test
    public void testStop() throws UnableToSendContentToServerException, ConnectionClosedException,
            DeserializationException {
        KVAdminMessage adminMessage = new KVAdminMessage.Builder().status(StatusType.STOP).build();
        serverCommunication.send(kvAdminMessageSerializer.serialize(adminMessage).getBytes());
        KVAdminMessage response =
                kvAdminMessageDeserializer.deserialize(serverCommunication.receive());

        Assert.assertEquals(StatusType.RESPONSE_SUCCESS, response.getStatus());
    }

    @Test
    public void testWriteLock() throws UnableToSendContentToServerException,
            ConnectionClosedException, DeserializationException {
        KVAdminMessage adminMessage =
                new KVAdminMessage.Builder().status(StatusType.LOCKWRITE).build();
        serverCommunication.send(kvAdminMessageSerializer.serialize(adminMessage).getBytes());
        KVAdminMessage response =
                kvAdminMessageDeserializer.deserialize(serverCommunication.receive());

        Assert.assertEquals(StatusType.RESPONSE_SUCCESS, response.getStatus());
    }

    @Test
    public void testWriteUnlock() throws UnableToSendContentToServerException,
            ConnectionClosedException, DeserializationException {
        KVAdminMessage adminMessage =
                new KVAdminMessage.Builder().status(StatusType.UNLOCKWRITE).build();
        serverCommunication.send(kvAdminMessageSerializer.serialize(adminMessage).getBytes());
        KVAdminMessage response =
                kvAdminMessageDeserializer.deserialize(serverCommunication.receive());

        Assert.assertEquals(StatusType.RESPONSE_SUCCESS, response.getStatus());
    }

    @Test
    public void testDataAccessServiceInitialization()
            throws UnknownHostException, UnableToSendContentToServerException,
            ConnectionClosedException, DeserializationException {
        ServerConnectionInfo server1 = new ServerConnectionInfo.Builder().ipAddress("localhost")
                .port(SERVER1_KVCLIENT_REQUEST_ACCEPTING_PORT).build();
        HashRange rangeForServer1 = new HashRange.Builder().begin(HashingUtil.getHash("a"))
                .end(HashingUtil.getHash("a")).build();
        RingMetadataPart part1 = new RingMetadataPart.Builder().connectionInfo(server1)
                .range(rangeForServer1).build();

        ServerConnectionInfo server2 = new ServerConnectionInfo.Builder().ipAddress("localhost")
                .port(SERVER2_KVCLIENT_REQUEST_ACCEPTING_PORT).build();
        HashRange rangeForServer2 = new HashRange.Builder().begin(HashingUtil.getHash("b"))
                .end(HashingUtil.getHash("b")).build();
        RingMetadataPart part2 = new RingMetadataPart.Builder().connectionInfo(server2)
                .range(rangeForServer2).build();

        RingMetadata ringMetadata = new RingMetadata(new HashSet<>(Arrays.asList(part1, part2)));

        KVAdminMessage adminMessage = new KVAdminMessage.Builder().status(StatusType.INITKVSERVER)
                .ringMetadata(ringMetadata).targetServerInfo(part1).build();

        serverCommunication.send(kvAdminMessageSerializer.serialize(adminMessage).getBytes());
        KVAdminMessage response =
                kvAdminMessageDeserializer.deserialize(serverCommunication.receive());

        Assert.assertEquals(StatusType.RESPONSE_SUCCESS, response.getStatus());
    }

    @Test
    public void testUpdate() throws UnableToSendContentToServerException, ConnectionClosedException,
            DeserializationException, UnknownHostException {
        ServerConnectionInfo server1 = new ServerConnectionInfo.Builder().ipAddress("localhost")
                .port(SERVER1_KVCLIENT_REQUEST_ACCEPTING_PORT).build();
        HashRange rangeForServer1 =
                new HashRange.Builder().begin(Hash.MIN_VALUE).end(Hash.MAX_VALUE).build();
        RingMetadataPart part1 = new RingMetadataPart.Builder().connectionInfo(server1)
                .range(rangeForServer1).build();

        ServerConnectionInfo server2 = new ServerConnectionInfo.Builder().ipAddress("localhost")
                .port(SERVER2_KVCLIENT_REQUEST_ACCEPTING_PORT).build();
        HashRange rangeForServer2 = new HashRange.Builder().begin(HashingUtil.getHash("1"))
                .end(HashingUtil.getHash("2")).build();
        RingMetadataPart part2 = new RingMetadataPart.Builder().connectionInfo(server2)
                .range(rangeForServer2).build();

        RingMetadata ringMetadata = new RingMetadata(new HashSet<>(Arrays.asList(part1)));

        KVAdminMessage adminMessage = new KVAdminMessage.Builder().status(StatusType.UPDATE)
                .ringMetadata(ringMetadata).targetServerInfo(part2).build();
        serverCommunication.send(kvAdminMessageSerializer.serialize(adminMessage).getBytes());
        KVAdminMessage response =
                kvAdminMessageDeserializer.deserialize(serverCommunication.receive());

        Assert.assertEquals(StatusType.RESPONSE_SUCCESS, response.getStatus());
    }

    @Test
    public void testMoveData() throws UnknownHostException, UnableToSendContentToServerException,
            ConnectionClosedException, DeserializationException {
        ServerConnectionInfo targetServer = new ServerConnectionInfo.Builder()
                .ipAddress("localhost").port(SERVER1_KVSERVER_REQUEST_ACCEPTING_PORT).build();
        HashRange targetRange = new HashRange.Builder().begin(HashingUtil.getHash("b"))
                .end(HashingUtil.getHash("b")).build();
        RingMetadataPart target = new RingMetadataPart.Builder().connectionInfo(targetServer)
                .range(targetRange).build();

        KVAdminMessage adminMessage = new KVAdminMessage.Builder().status(StatusType.MOVEDATA)
                .targetServerInfo(target).build();

        serverCommunication.send(kvAdminMessageSerializer.serialize(adminMessage).getBytes());
        KVAdminMessage response =
                kvAdminMessageDeserializer.deserialize(serverCommunication.receive());

        Assert.assertEquals(StatusType.RESPONSE_SUCCESS, response.getStatus());
    }

    @Test
    public void testShutdown() throws UnableToSendContentToServerException,
            ConnectionClosedException, DeserializationException {
        KVAdminMessage adminMessage =
                new KVAdminMessage.Builder().status(StatusType.SHUTDOWN).build();
        serverCommunication.send(kvAdminMessageSerializer.serialize(adminMessage).getBytes());
        KVAdminMessage response =
                kvAdminMessageDeserializer.deserialize(serverCommunication.receive());

        Assert.assertEquals(StatusType.RESPONSE_SUCCESS, response.getStatus());
    }



}
