package testing.weloveclouds.kvstore.serialization;

import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import junit.framework.TestCase;
import weloveclouds.commons.hashing.models.Hash;
import weloveclouds.commons.hashing.models.HashRange;
import weloveclouds.commons.hashing.models.RingMetadataPart;
import weloveclouds.commons.hashing.utils.HashingUtil;
import weloveclouds.commons.kvstore.deserialization.exceptions.DeserializationException;
import weloveclouds.commons.kvstore.deserialization.helper.IDeserializer;
import weloveclouds.commons.kvstore.deserialization.helper.RingMetadataPartDeserializer;
import weloveclouds.commons.kvstore.serialization.helper.ISerializer;
import weloveclouds.commons.kvstore.serialization.helper.RingMetadataPartSerializer;
import weloveclouds.communication.models.ServerConnectionInfo;


/**
 * Tests for the {@link RingMetadataPart} to verify its serialization and deserialization processes.
 * 
 * @author Benedek
 */
public class RingMetadataPartTest extends TestCase {

    private static final IDeserializer<RingMetadataPart, String> metadataPartDeserializer =
            new RingMetadataPartDeserializer();
    private static final ISerializer<String, RingMetadataPart> metadataPartSerializer =
            new RingMetadataPartSerializer();

    @Test
    public void testRingMetadataPartSerializationAndDeserialization()
            throws DeserializationException, UnknownHostException {
        ServerConnectionInfo sci =
                new ServerConnectionInfo.Builder().ipAddress("localhost").port(8080).build();
        HashRange range1 =
                new HashRange.Builder().begin(Hash.MIN_VALUE).end(Hash.MAX_VALUE).build();
        HashRange writeRange = new HashRange.Builder().begin(HashingUtil.getHash("a"))
                .end(HashingUtil.getHash("a")).build();
        Set<HashRange> readRanges = new HashSet<>(Arrays.asList(range1, writeRange));

        RingMetadataPart metadataPart = new RingMetadataPart.Builder().connectionInfo(sci)
                .readRanges(readRanges).writeRange(writeRange).build();

        String serializedMetadataPart = metadataPartSerializer.serialize(metadataPart);
        RingMetadataPart deserializedMetadataPart =
                metadataPartDeserializer.deserialize(serializedMetadataPart);

        Assert.assertEquals(metadataPart.toString(), deserializedMetadataPart.toString());
        Assert.assertEquals(metadataPart, deserializedMetadataPart);
    }
}
