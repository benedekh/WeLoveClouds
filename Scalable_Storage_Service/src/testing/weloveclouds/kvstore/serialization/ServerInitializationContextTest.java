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
import weloveclouds.kvstore.deserialization.helper.IDeserializer;
import weloveclouds.kvstore.deserialization.helper.ServerInitializationContextDeserializer;
import weloveclouds.kvstore.serialization.exceptions.DeserializationException;
import weloveclouds.kvstore.serialization.helper.ISerializer;
import weloveclouds.kvstore.serialization.helper.ServerInitializationContextSerializer;
import weloveclouds.server.models.ServerInitializationContext;

public class ServerInitializationContextTest {

    private static final IDeserializer<ServerInitializationContext, String> contextDeserializer =
            new ServerInitializationContextDeserializer();
    private static final ISerializer<String, ServerInitializationContext> contextSerializer =
            new ServerInitializationContextSerializer();

    @Test
    public void testServerInitializationContextSerializationAndDeserialization()
            throws UnknownHostException, DeserializationException {
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

        String serializedContext = contextSerializer.serialize(context);
        ServerInitializationContext deserializedContext =
                contextDeserializer.deserialize(serializedContext);

        Assert.assertEquals(context.toString(), deserializedContext.toString());
        Assert.assertEquals(context, deserializedContext);
    }
}
