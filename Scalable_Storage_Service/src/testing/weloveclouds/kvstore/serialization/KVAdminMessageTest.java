package testing.weloveclouds.kvstore.serialization;

import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashSet;

import org.junit.Test;

import junit.framework.Assert;
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
import weloveclouds.server.models.ServerInitializationContext;

public class KVAdminMessageTest {

    private static IMessageDeserializer<KVAdminMessage, SerializedMessage> adminMessageDeserializer =
            new KVAdminMessageDeserializer();
    private static IMessageSerializer<SerializedMessage, KVAdminMessage> adminMessageSerializer =
            new KVAdminMessageSerializer();

    @Test
    public void testKVAdminMessageSerializationAndDeserialization()
            throws DeserializationException, UnknownHostException {
        HashRange managedRange = new HashRange.Builder().start(HashingUtil.getHash("a"))
                .end(HashingUtil.getHash("b")).build();
        RingMetadataPart metadataPart =
                new RingMetadataPart.Builder()
                        .connectionInfo(new ServerConnectionInfo.ServerConnectionInfoBuilder()
                                .ipAddress("localhost").port(8080).build())
                        .range(managedRange).build();
        RingMetadata metadata = new RingMetadata(new HashSet<>(Arrays.asList(metadataPart,
                new RingMetadataPart.Builder()
                        .connectionInfo(new ServerConnectionInfo.ServerConnectionInfoBuilder()
                                .ipAddress("localhost").port(8082).build())
                        .range(new HashRange.Builder().start(Hash.MIN_VALUE).end(Hash.MAX_VALUE)
                                .build())
                        .build())));
        ServerInitializationContext context = new ServerInitializationContext.Builder()
                .ringMetadata(metadata).rangeManagedByServer(managedRange).cacheSize(10)
                .displacementStrategyName("LRU").build();

        KVAdminMessage adminMessage = new KVAdminMessage.KVAdminMessageBuilder()
                .initializationContext(context).status(StatusType.INITKVSERVER).build();

        SerializedMessage serializedMessage = adminMessageSerializer.serialize(adminMessage);
        KVAdminMessage deserializedAdminMessage =
                adminMessageDeserializer.deserialize(serializedMessage);

        Assert.assertEquals(adminMessage.toString(), deserializedAdminMessage.toString());
        Assert.assertEquals(adminMessage, deserializedAdminMessage);
    }

}

