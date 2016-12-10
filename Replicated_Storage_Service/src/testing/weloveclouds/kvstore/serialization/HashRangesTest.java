package testing.weloveclouds.kvstore.serialization;


import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashSet;

import org.junit.Test;

import junit.framework.Assert;
import junit.framework.TestCase;
import weloveclouds.hashing.models.Hash;
import weloveclouds.hashing.models.HashRange;
import weloveclouds.hashing.models.HashRanges;
import weloveclouds.hashing.utils.HashingUtil;
import weloveclouds.kvstore.deserialization.helper.HashRangesDeserializer;
import weloveclouds.kvstore.deserialization.helper.IDeserializer;
import weloveclouds.kvstore.serialization.exceptions.DeserializationException;
import weloveclouds.kvstore.serialization.helper.HashRangesSerializer;
import weloveclouds.kvstore.serialization.helper.ISerializer;

/**
 * Tests for the {@link HashRanges} to verify its serialization and deserialization processes.
 * 
 * @author Benedek
 */
public class HashRangesTest extends TestCase {

    private static final IDeserializer<HashRanges, String> hashRangesDeserializer =
            new HashRangesDeserializer();
    private static final ISerializer<String, HashRanges> hashRangesSerializer =
            new HashRangesSerializer();

    @Test
    public void testHashRangeSerializationAndDeserialization()
            throws DeserializationException, UnknownHostException {
        HashRange range1 =
                new HashRange.Builder().begin(Hash.MIN_VALUE).end(Hash.MAX_VALUE).build();
        HashRange range2 = new HashRange.Builder().begin(HashingUtil.getHash("a"))
                .end(HashingUtil.getHash("a")).build();
        HashRanges hashRanges = new HashRanges(new HashSet<>(Arrays.asList(range1, range2)));
        
        String serializedRangess = hashRangesSerializer.serialize(hashRanges);
        HashRanges deserializedRanges = hashRangesDeserializer.deserialize(serializedRangess);

        Assert.assertEquals(hashRanges.toString(), deserializedRanges.toString());
        Assert.assertEquals(hashRanges, deserializedRanges);
    }

}
