package testing.weloveclouds.kvstore.serialization;

import java.net.UnknownHostException;

import org.junit.Test;

import junit.framework.Assert;
import junit.framework.TestCase;
import weloveclouds.commons.hashing.models.Hash;
import weloveclouds.commons.hashing.models.HashRange;
import weloveclouds.commons.hashing.utils.HashingUtils;
import weloveclouds.commons.kvstore.deserialization.exceptions.DeserializationException;
import weloveclouds.commons.kvstore.deserialization.helper.HashRangeDeserializer;
import weloveclouds.commons.kvstore.serialization.helper.HashRangeSerializer;
import weloveclouds.commons.serialization.IDeserializer;
import weloveclouds.commons.serialization.ISerializer;
import weloveclouds.commons.serialization.models.AbstractXMLNode;

/**
 * Tests for the {@link HashRange} to verify its serialization and deserialization processes.
 * 
 * @author Benedek
 */
public class HashRangeTest extends TestCase {

    private static final IDeserializer<HashRange, String> hashRangeDeserializer =
            new HashRangeDeserializer();
    private static final ISerializer<AbstractXMLNode, HashRange> hashRangeSerializer =
            new HashRangeSerializer();

    @Test
    public void testHashRangeSerializationAndDeserialization()
            throws DeserializationException, UnknownHostException {
        Hash start = HashingUtils.getHash("a");
        Hash end = HashingUtils.getHash("z");
        HashRange range = new HashRange.Builder().begin(start).end(end).build();

        String serializedRange = hashRangeSerializer.serialize(range).toString();
        HashRange deserializedRange = hashRangeDeserializer.deserialize(serializedRange);

        Assert.assertEquals(range.toString(), deserializedRange.toString());
        Assert.assertEquals(range, deserializedRange);
    }

}
