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
import weloveclouds.kvstore.deserialization.helper.RingMetadataDeserializer;
import weloveclouds.kvstore.serialization.exceptions.DeserializationException;
import weloveclouds.kvstore.serialization.helper.ISerializer;
import weloveclouds.kvstore.serialization.helper.RingMetadataSerializer;

public class RingMetadataTest {

    private static final IDeserializer<RingMetadata, String> metadataDeserializer =
            new RingMetadataDeserializer();
    private static final ISerializer<String, RingMetadata> metadataSerializer =
            new RingMetadataSerializer();

    @Test
    public void testMovableStorageUnitSerializationAndDeserialization()
            throws DeserializationException, UnknownHostException {

        RingMetadataPart metadataPart1 = new RingMetadataPart.RingMetadataPartBuilder()
                .connectionInfo(new ServerConnectionInfo.ServerConnectionInfoBuilder()
                        .ipAddress("localhost").port(8080).build())
                .range(new HashRange(HashingUtil.getHash("a"), HashingUtil.getHash("b"))).build();

        RingMetadataPart metadataPart2 = new RingMetadataPart.RingMetadataPartBuilder()
                .connectionInfo(new ServerConnectionInfo.ServerConnectionInfoBuilder()
                        .ipAddress("localhost").port(8082).build())
                .range(new HashRange(Hash.MIN_VALUE, Hash.MAX_VALUE)).build();

        RingMetadata metadata =
                new RingMetadata(new HashSet<>(Arrays.asList(metadataPart1, metadataPart2)));

        String serializedMetadata = metadataSerializer.serialize(metadata);
        RingMetadata deserializedMetadata = metadataDeserializer.deserialize(serializedMetadata);

        Assert.assertEquals(metadata.toString(), deserializedMetadata.toString());
        Assert.assertEquals(metadata, deserializedMetadata);
    }

}
