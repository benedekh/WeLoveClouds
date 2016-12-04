package testing.util;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

import weloveclouds.communication.CommunicationApiFactory;
import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.ecs.core.ExternalConfigurationServiceConstants;
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
import weloveclouds.kvstore.serialization.models.SerializedMessage;
import weloveclouds.server.api.KVCommunicationApiFactory;
import weloveclouds.server.api.v2.IKVCommunicationApiV2;

public class KVServerInitializationUtil implements AutoCloseable {

    private static final String SERVER_IP_ADDRESS = "localhost";
    private static final int SERVER_KVCLIENT_PORT = 50000;

    private static final HashRange EVERY_HASH =
            new HashRange.Builder().begin(Hash.MIN_VALUE).end(Hash.MAX_VALUE).build();

    IKVCommunicationApiV2 serverCommunication;
    IMessageDeserializer<KVAdminMessage, SerializedMessage> kvAdminMessageDeserializer;
    IMessageSerializer<SerializedMessage, KVAdminMessage> kvAdminMessageSerializer;

    public KVServerInitializationUtil() throws Exception {
        ServerConnectionInfo bootstrapConnectionInfo =
                new ServerConnectionInfo.Builder().ipAddress(SERVER_IP_ADDRESS)
                        .port(ExternalConfigurationServiceConstants.ECS_REQUESTS_PORT).build();
        serverCommunication =
                new KVCommunicationApiFactory().createKVCommunicationApiV2(bootstrapConnectionInfo);

        kvAdminMessageDeserializer = new KVAdminMessageDeserializer();
        kvAdminMessageSerializer = new KVAdminMessageSerializer();
    }

    @Override
    public void close() throws Exception {
        serverCommunication.disconnect();
    }

    public void connect() throws Exception {
        serverCommunication.connect();
    }

    public void updateRingMetadataServerHandlesOnlyHashA() throws Exception {
        updateRingMetadata(new HashRange.Builder().begin(HashingUtil.getHash("A"))
                .end(HashingUtil.getHash("A")).build());
    }

    public void updateRingMetadataServerHandlesEveryHash() throws Exception {
        updateRingMetadata(EVERY_HASH);
    }

    public void initializeServerHandlesEveryHash() throws Exception {
        RingMetadataPart part = createRingMetadataPart(EVERY_HASH);

        RingMetadata ringMetadata = new RingMetadata(new HashSet<>(Arrays.asList(part)));
        KVAdminMessage adminMessage = new KVAdminMessage.Builder().status(StatusType.INITKVSERVER)
                .ringMetadata(ringMetadata).targetServerInfo(part).build();

        serverCommunication.send(kvAdminMessageSerializer.serialize(adminMessage).getBytes());
        KVAdminMessage response =
                kvAdminMessageDeserializer.deserialize(serverCommunication.receive());

        if (response.getStatus() == StatusType.RESPONSE_ERROR) {
            throw new Exception(response.getResponseMessage());
        }
    }

    public void startServer() throws Exception {
        KVAdminMessage adminMessage = new KVAdminMessage.Builder().status(StatusType.START).build();
        serverCommunication.send(kvAdminMessageSerializer.serialize(adminMessage).getBytes());
        KVAdminMessage response =
                kvAdminMessageDeserializer.deserialize(serverCommunication.receive());
        if (response.getStatus() == StatusType.RESPONSE_ERROR) {
            throw new Exception(response.getResponseMessage());
        }
    }

    private void updateRingMetadata(HashRange range) throws Exception {
        RingMetadataPart part = createRingMetadataPart(range);
        RingMetadata ringMetadata = new RingMetadata(new HashSet<>(Arrays.asList(part)));

        KVAdminMessage adminMessage = new KVAdminMessage.Builder().status(StatusType.UPDATE)
                .ringMetadata(ringMetadata).targetServerInfo(part).build();
        serverCommunication.send(kvAdminMessageSerializer.serialize(adminMessage).getBytes());
        KVAdminMessage response =
                kvAdminMessageDeserializer.deserialize(serverCommunication.receive());

        if (response.getStatus() == StatusType.RESPONSE_ERROR) {
            throw new Exception(response.getResponseMessage());
        }
    }

    private RingMetadataPart createRingMetadataPart(HashRange range) throws IOException {
        ServerConnectionInfo server = new ServerConnectionInfo.Builder().ipAddress("localhost")
                .port(SERVER_KVCLIENT_PORT).build();
        return new RingMetadataPart.Builder().connectionInfo(server).range(range).build();
    }


}
