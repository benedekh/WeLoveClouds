package testing.weloveclouds.server.requests;

import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import junit.framework.Assert;
import weloveclouds.communication.exceptions.ConnectionClosedException;
import weloveclouds.communication.exceptions.UnableToSendContentToServerException;
import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.hashing.models.Hash;
import weloveclouds.hashing.models.HashRange;
import weloveclouds.hashing.models.RingMetadata;
import weloveclouds.hashing.models.RingMetadataPart;
import weloveclouds.hashing.utils.HashingUtil;
import weloveclouds.kvstore.deserialization.IMessageDeserializer;
import weloveclouds.kvstore.deserialization.KVAdminMessageDeserializer;
import weloveclouds.kvstore.models.messages.IKVAdminMessage.StatusType;
import weloveclouds.kvstore.models.messages.KVAdminMessage;
import weloveclouds.kvstore.serialization.IMessageSerializer;
import weloveclouds.kvstore.serialization.KVAdminMessageSerializer;
import weloveclouds.kvstore.serialization.exceptions.DeserializationException;
import weloveclouds.kvstore.serialization.models.SerializedMessage;
import weloveclouds.server.api.KVCommunicationApiFactory;
import weloveclouds.server.api.v2.IKVCommunicationApiV2;
import weloveclouds.server.models.configuration.KVServerPortConstants;

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
        ServerConnectionInfo bootstrapConnectionInfo =
                new ServerConnectionInfo.Builder().ipAddress(SERVER_IP_ADDRESS).port(30000).build();
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
        HashRange range1 = new HashRange.Builder().begin(HashingUtil.getHash("a"))
                .end(HashingUtil.getHash("a")).build();
        Set<HashRange> readRanges = new HashSet<>(Arrays.asList(range1));
        RingMetadataPart part1 = new RingMetadataPart.Builder().connectionInfo(server1)
                .readRanges(readRanges).writeRange(range1).build();

        ServerConnectionInfo server2 =
                new ServerConnectionInfo.Builder().ipAddress("localhost").port(50003).build();
        RingMetadataPart part2 = new RingMetadataPart.Builder().connectionInfo(server2)
                .readRanges(readRanges).build();

        ServerConnectionInfo server3 =
                new ServerConnectionInfo.Builder().ipAddress("localhost").port(50005).build();
        RingMetadataPart part3 = new RingMetadataPart.Builder().connectionInfo(server3)
                .readRanges(readRanges).build();

        RingMetadata ringMetadata =
                new RingMetadata(new HashSet<>(Arrays.asList(part1, part2, part3)));
        Set<ServerConnectionInfo> serverConnectionInfos =
                new HashSet<>(Arrays.asList(server2, server3));

        KVAdminMessage adminMessage = new KVAdminMessage.Builder().status(StatusType.INITKVSERVER)
                .ringMetadata(ringMetadata).build();

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
        HashRange writeRange =
                new HashRange.Builder().begin(Hash.MIN_VALUE).end(Hash.MAX_VALUE).build();
        RingMetadataPart part1 = new RingMetadataPart.Builder().connectionInfo(server1)
                .writeRange(writeRange).build();

        ServerConnectionInfo server2 = new ServerConnectionInfo.Builder().ipAddress("localhost")
                .port(SERVER2_KVCLIENT_REQUEST_ACCEPTING_PORT).build();
        HashRange writeRange2 = new HashRange.Builder().begin(HashingUtil.getHash("1"))
                .end(HashingUtil.getHash("2")).build();
        RingMetadataPart part2 = new RingMetadataPart.Builder().connectionInfo(server2)
                .writeRange(writeRange2).build();

        RingMetadata ringMetadata = new RingMetadata(new HashSet<>(Arrays.asList(part1)));

        KVAdminMessage adminMessage = new KVAdminMessage.Builder().status(StatusType.UPDATE)
                .ringMetadata(ringMetadata).targetServerInfo(part2).build();
        serverCommunication.send(kvAdminMessageSerializer.serialize(adminMessage).getBytes());
        KVAdminMessage response =
                kvAdminMessageDeserializer.deserialize(serverCommunication.receive());

        Assert.assertEquals(StatusType.RESPONSE_SUCCESS, response.getStatus());
    }

    @Test
    public void testRemoveRange() throws UnknownHostException, UnableToSendContentToServerException,
            ConnectionClosedException, DeserializationException {
        HashRange targetRange = new HashRange.Builder().begin(HashingUtil.getHash("b"))
                .end(HashingUtil.getHash("b")).build();

        KVAdminMessage adminMessage = new KVAdminMessage.Builder().status(StatusType.COPYDATA)
                .removableRange(targetRange).build();

        serverCommunication.send(kvAdminMessageSerializer.serialize(adminMessage).getBytes());
        KVAdminMessage response =
                kvAdminMessageDeserializer.deserialize(serverCommunication.receive());

        Assert.assertEquals(StatusType.RESPONSE_SUCCESS, response.getStatus());
    }

    @Test
    public void testCopyData() throws UnknownHostException, UnableToSendContentToServerException,
            ConnectionClosedException, DeserializationException {
        ServerConnectionInfo targetServer = new ServerConnectionInfo.Builder()
                .ipAddress("localhost").port(SERVER1_KVSERVER_REQUEST_ACCEPTING_PORT).build();
        HashRange targetRange = new HashRange.Builder().begin(HashingUtil.getHash("b"))
                .end(HashingUtil.getHash("b")).build();
        RingMetadataPart target = new RingMetadataPart.Builder().connectionInfo(targetServer)
                .writeRange(targetRange).build();

        KVAdminMessage adminMessage = new KVAdminMessage.Builder().status(StatusType.COPYDATA)
                .targetServerInfo(target).build();

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
                .writeRange(targetRange).build();

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
