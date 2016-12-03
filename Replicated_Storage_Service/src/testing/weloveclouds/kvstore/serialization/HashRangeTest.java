package testing.weloveclouds.kvstore.serialization;

import java.net.UnknownHostException;

import org.junit.Test;

import junit.framework.Assert;
import junit.framework.TestCase;
import weloveclouds.commons.hashing.models.Hash;
import weloveclouds.commons.hashing.models.HashRange;
import weloveclouds.commons.hashing.utils.HashingUtil;
import weloveclouds.kvstore.deserialization.helper.HashRangeDeserializer;
import weloveclouds.kvstore.deserialization.helper.IDeserializer;
import weloveclouds.kvstore.serialization.exceptions.DeserializationException;
import weloveclouds.kvstore.serialization.helper.HashRangeSerializer;
import weloveclouds.kvstore.serialization.helper.ISerializer;

/**
 * Tests for the {@link HashRange} to verify its serialization and deserialization processes.
 * 
 * @author Benedek
 */
public class HashRangeTest extends TestCase {

    private static final IDeserializer<HashRange, String> hashRangeDeserializer =
            new HashRangeDeserializer();
    private static final ISerializer<String, HashRange> hashRangeSerializer =
            new HashRangeSerializer();

    @Test
    public void testHashRangeSerializationAndDeserialization()
            throws DeserializationException, UnknownHostException {
        Hash start = HashingUtil.getHash("a");
        Hash end = HashingUtil.getHash("z");
        HashRange range = new HashRange.Builder().begin(start).end(end).build();

        String serializedRange = hashRangeSerializer.serialize(range);
        HashRange deserializedRange = hashRangeDeserializer.deserialize(serializedRange);

        Assert.assertEquals(range.toString(), deserializedRange.toString());
        Assert.assertEquals(range, deserializedRange);
    }

}
