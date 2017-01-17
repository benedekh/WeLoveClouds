package testing.utils;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

import weloveclouds.commons.hashing.models.Hash;
import weloveclouds.commons.hashing.models.HashRange;
import weloveclouds.commons.hashing.models.RingMetadata;
import weloveclouds.commons.hashing.models.RingMetadataPart;
import weloveclouds.commons.hashing.utils.HashingUtils;
import weloveclouds.commons.kvstore.deserialization.KVAdminMessageDeserializer;
import weloveclouds.commons.kvstore.models.messages.IKVAdminMessage;
import weloveclouds.commons.kvstore.models.messages.IKVAdminMessage.StatusType;
import weloveclouds.commons.kvstore.models.messages.KVAdminMessage;
import weloveclouds.commons.kvstore.serialization.KVAdminMessageSerializer;
import weloveclouds.commons.serialization.IMessageDeserializer;
import weloveclouds.commons.serialization.IMessageSerializer;
import weloveclouds.commons.serialization.models.SerializedMessage;
import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.ecs.core.ExternalConfigurationServiceConstants;
import weloveclouds.server.api.KVCommunicationApiFactory;
import weloveclouds.server.api.v2.IKVCommunicationApiV2;

/**
 * Utility class which initializes the KVServer and updates its ring metadata.
 * 
 * @author Benedek
 */
public class KVServerInitializationUtils implements AutoCloseable {

    private static final String SERVER_IP_ADDRESS = "localhost";
    private static final int SERVER_KVCLIENT_PORT = 50000;

    private static final HashRange EVERY_HASH =
            new HashRange.Builder().begin(Hash.MIN_VALUE).end(Hash.MAX_VALUE).build();

    private IKVCommunicationApiV2 serverCommunication;
    private IMessageDeserializer<IKVAdminMessage, SerializedMessage> kvAdminMessageDeserializer;
    private IMessageSerializer<SerializedMessage, IKVAdminMessage> kvAdminMessageSerializer;

    public KVServerInitializationUtils() throws Exception {
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

    /**
     * Update the KVServer's ring metadata, so that it only handles "A" keys.
     * 
     * @throws Exception in case an error occurs
     */
    public void updateRingMetadataServerHandlesOnlyHashA() throws Exception {
        updateRingMetadata(new HashRange.Builder().begin(HashingUtils.getHash("A"))
                .end(HashingUtils.getHash("A")).build());
    }

    /**
     * Update the KVServer's ring metadata, so that it handles every hash.
     * 
     * @throws Exception in case an error occurs
     */
    public void updateRingMetadataServerHandlesEveryHash() throws Exception {
        updateRingMetadata(EVERY_HASH);
    }

    /**
     * Initializes the KVServer, so that it handles every hash.
     * 
     * @throws Exception in case an error occurs
     */
    public void initializeServerHandlesEveryHash() throws Exception {
        RingMetadataPart part = createRingMetadataPart(EVERY_HASH);

        RingMetadata ringMetadata = new RingMetadata(new HashSet<>(Arrays.asList(part)));
        KVAdminMessage adminMessage = new KVAdminMessage.Builder().status(StatusType.INITKVSERVER)
                .ringMetadata(ringMetadata).targetServerInfo(part).build();

        serverCommunication.send(kvAdminMessageSerializer.serialize(adminMessage).getBytes());
        IKVAdminMessage response =
                kvAdminMessageDeserializer.deserialize(serverCommunication.receive());

        if (response.getStatus() == StatusType.RESPONSE_ERROR) {
            throw new Exception(response.getResponseMessage());
        }
    }

    /**
     * Starts the KVServer.
     * 
     * @throws Exception in case an error occurs
     */
    public void startServer() throws Exception {
        KVAdminMessage adminMessage = new KVAdminMessage.Builder().status(StatusType.START).build();
        serverCommunication.send(kvAdminMessageSerializer.serialize(adminMessage).getBytes());
        IKVAdminMessage response =
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
        IKVAdminMessage response =
                kvAdminMessageDeserializer.deserialize(serverCommunication.receive());

        if (response.getStatus() == StatusType.RESPONSE_ERROR) {
            throw new Exception(response.getResponseMessage());
        }
    }

    private RingMetadataPart createRingMetadataPart(HashRange range) throws IOException {
        ServerConnectionInfo server = new ServerConnectionInfo.Builder().ipAddress("localhost")
                .port(SERVER_KVCLIENT_PORT).build();

        return new RingMetadataPart.Builder().connectionInfo(server)
                .readRanges(new HashSet<>(Arrays.asList(range))).writeRange(range).build();
    }

}
