package testing.weloveclouds.kvstore.serialization;

import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import junit.framework.Assert;
import junit.framework.TestCase;
import weloveclouds.commons.hashing.models.Hash;
import weloveclouds.commons.hashing.models.HashRange;
import weloveclouds.commons.hashing.models.RingMetadata;
import weloveclouds.commons.hashing.models.RingMetadataPart;
import weloveclouds.commons.hashing.utils.HashingUtils;
import weloveclouds.commons.kvstore.deserialization.exceptions.DeserializationException;
import weloveclouds.commons.kvstore.deserialization.helper.RingMetadataDeserializer;
import weloveclouds.commons.kvstore.serialization.helper.RingMetadataSerializer;
import weloveclouds.commons.serialization.IDeserializer;
import weloveclouds.commons.serialization.ISerializer;
import weloveclouds.commons.serialization.models.AbstractXMLNode;
import weloveclouds.communication.models.ServerConnectionInfo;

/**
 * Tests for the {@link RingMetadata} to verify its serialization and deserialization processes.
 * 
 * @author Benedek
 */
public class RingMetadataTest extends TestCase {

    private static final IDeserializer<RingMetadata, String> metadataDeserializer =
            new RingMetadataDeserializer();
    private static final ISerializer<AbstractXMLNode, RingMetadata> metadataSerializer =
            new RingMetadataSerializer();

    @Test
    public void testMovableStorageUnitSerializationAndDeserialization()
            throws DeserializationException, UnknownHostException {
        HashRange range1 =
                new HashRange.Builder().begin(Hash.MIN_VALUE).end(Hash.MAX_VALUE).build();
        HashRange writeRange = new HashRange.Builder().begin(HashingUtils.getHash("a"))
                .end(HashingUtils.getHash("a")).build();
        Set<HashRange> readRanges = new HashSet<>(Arrays.asList(range1, writeRange));
        RingMetadataPart metadataPart1 =
                new RingMetadataPart.Builder()
                        .connectionInfo(new ServerConnectionInfo.Builder().ipAddress("localhost")
                                .port(8080).build())
                        .readRanges(readRanges).writeRange(writeRange).build();

        RingMetadataPart metadataPart2 = new RingMetadataPart.Builder().connectionInfo(
                new ServerConnectionInfo.Builder().ipAddress("localhost").port(8082).build())
                .readRanges(readRanges).build();

        RingMetadata metadata =
                new RingMetadata(new HashSet<>(Arrays.asList(metadataPart1, metadataPart2)));

        String serializedMetadata = metadataSerializer.serialize(metadata).toString();
        RingMetadata deserializedMetadata = metadataDeserializer.deserialize(serializedMetadata);

        Assert.assertEquals(metadata.toString(), deserializedMetadata.toString());
        Assert.assertEquals(metadata, deserializedMetadata);
    }

}
