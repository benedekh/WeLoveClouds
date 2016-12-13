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
import weloveclouds.commons.hashing.utils.HashingUtils;
import weloveclouds.commons.kvstore.deserialization.exceptions.DeserializationException;
import weloveclouds.commons.kvstore.deserialization.helper.RingMetadataPartDeserializer;
import weloveclouds.commons.kvstore.serialization.helper.RingMetadataPartSerializer;
import weloveclouds.commons.serialization.IDeserializer;
import weloveclouds.commons.serialization.ISerializer;
import weloveclouds.commons.serialization.models.AbstractXMLNode;
import weloveclouds.communication.models.ServerConnectionInfo;


/**
 * Tests for the {@link RingMetadataPart} to verify its serialization and deserialization processes.
 * 
 * @author Benedek
 */
public class RingMetadataPartTest extends TestCase {

    private static final IDeserializer<RingMetadataPart, String> metadataPartDeserializer =
            new RingMetadataPartDeserializer();
    private static final ISerializer<AbstractXMLNode, RingMetadataPart> metadataPartSerializer =
            new RingMetadataPartSerializer();

    @Test
    public void testRingMetadataPartSerializationAndDeserialization()
            throws DeserializationException, UnknownHostException {
        ServerConnectionInfo sci =
                new ServerConnectionInfo.Builder().ipAddress("localhost").port(8080).build();
        HashRange range1 =
                new HashRange.Builder().begin(Hash.MIN_VALUE).end(Hash.MAX_VALUE).build();
        HashRange writeRange = new HashRange.Builder().begin(HashingUtils.getHash("a"))
                .end(HashingUtils.getHash("a")).build();
        Set<HashRange> readRanges = new HashSet<>(Arrays.asList(range1, writeRange));

        RingMetadataPart metadataPart = new RingMetadataPart.Builder().connectionInfo(sci)
                .readRanges(readRanges).writeRange(writeRange).build();

        String serializedMetadataPart = metadataPartSerializer.serialize(metadataPart).toString();
        RingMetadataPart deserializedMetadataPart =
                metadataPartDeserializer.deserialize(serializedMetadataPart);

        Assert.assertEquals(metadataPart.toString(), deserializedMetadataPart.toString());
        Assert.assertEquals(metadataPart, deserializedMetadataPart);
    }
}
