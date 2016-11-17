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

    private static final IDeserializer<ServerInitializationContext, String> initializiationContextDeserializer =
            new ServerInitializationContextDeserializer();
    private static final ISerializer<String, ServerInitializationContext> initializiationContextSerializer =
            new ServerInitializationContextSerializer();

    @Test
    public void testServerInitializationContextSerializationAndDeserialization()
            throws UnknownHostException, DeserializationException {
        RingMetadata metadata = new RingMetadata(new HashSet<>(Arrays.asList(
                new RingMetadataPart.RingMetadataPartBuilder()
                        .connectionInfo(new ServerConnectionInfo.ServerConnectionInfoBuilder()
                                .ipAddress("localhost").port(8080).build())
                        .range(new HashRange(HashingUtil.getHash("a"), HashingUtil.getHash("b")))
                        .build(),
                new RingMetadataPart.RingMetadataPartBuilder()
                        .connectionInfo(new ServerConnectionInfo.ServerConnectionInfoBuilder()
                                .ipAddress("localhost").port(8082).build())
                        .range(new HashRange(Hash.MIN_VALUE, Hash.MAX_VALUE)).build())));
        int cacheSize = 10;
        String displacementStrategyName = "LRU";

        ServerInitializationContext initializationContext =
                new ServerInitializationContext(metadata, cacheSize, displacementStrategyName);

        String serializedInitializationContext =
                initializiationContextSerializer.serialize(initializationContext);
        ServerInitializationContext deserializedInitializationContext =
                initializiationContextDeserializer.deserialize(serializedInitializationContext);

        Assert.assertEquals(initializationContext.toString(),
                deserializedInitializationContext.toString());
        Assert.assertEquals(initializationContext, deserializedInitializationContext);
    }
}
