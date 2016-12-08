package testing.weloveclouds.kvstore.serialization;

import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashSet;

import org.junit.Test;

import junit.framework.Assert;
import junit.framework.TestCase;
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
import weloveclouds.server.models.replication.HashRangeWithRole;
import weloveclouds.server.models.replication.Role;

/**
 * Tests for the {@link RingMetadata} to verify its serialization and deserialization processes.
 * 
 * @author Benedek
 */
public class RingMetadataTest extends TestCase {

    private static final IDeserializer<RingMetadata, String> metadataDeserializer =
            new RingMetadataDeserializer();
    private static final ISerializer<String, RingMetadata> metadataSerializer =
            new RingMetadataSerializer();

    @Test
    public void testMovableStorageUnitSerializationAndDeserialization()
            throws DeserializationException, UnknownHostException {
        HashRangeWithRole hashRangeWithRole1 = new HashRangeWithRole.Builder()
                .hashRange(new HashRange.Builder().begin(HashingUtil.getHash("a"))
                        .end(HashingUtil.getHash("b")).build())
                .role(Role.MASTER).build();
        RingMetadataPart metadataPart1 = new RingMetadataPart.Builder().connectionInfo(
                new ServerConnectionInfo.Builder().ipAddress("localhost").port(8080).build())
                .rangeWithRole(hashRangeWithRole1).build();

        HashRangeWithRole hashRangeWithRole2 = new HashRangeWithRole.Builder()
                .hashRange(
                        new HashRange.Builder().begin(Hash.MIN_VALUE).end(Hash.MAX_VALUE).build())
                .role(Role.MASTER).build();
        RingMetadataPart metadataPart2 = new RingMetadataPart.Builder().connectionInfo(
                new ServerConnectionInfo.Builder().ipAddress("localhost").port(8082).build())
                .rangeWithRole(hashRangeWithRole2).build();

        RingMetadata metadata =
                new RingMetadata(new HashSet<>(Arrays.asList(metadataPart1, metadataPart2)));

        String serializedMetadata = metadataSerializer.serialize(metadata);
        RingMetadata deserializedMetadata = metadataDeserializer.deserialize(serializedMetadata);

        Assert.assertEquals(metadata.toString(), deserializedMetadata.toString());
        Assert.assertEquals(metadata, deserializedMetadata);
    }

}
