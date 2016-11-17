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

    public static final IDeserializer<ServerInitializationContext, String> deserializer =
            new ServerInitializationContextDeserializer();
    public static final ISerializer<String, ServerInitializationContext> serializer =
            new ServerInitializationContextSerializer();


    @Test
    public void test() throws UnknownHostException, DeserializationException {
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

        ServerInitializationContext context =
                new ServerInitializationContext(metadata, cacheSize, displacementStrategyName);

        String ser = serializer.serialize(context);
        ServerInitializationContext deser = deserializer.deserialize(ser);

        Assert.assertEquals(context.toString(), deser.toString());
        Assert.assertEquals(context, deser);
    }
}
