package testing.weloveclouds.kvstore.serialization;


import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import junit.framework.Assert;
import junit.framework.TestCase;
import weloveclouds.hashing.models.Hash;
import weloveclouds.hashing.models.HashRange;
import weloveclouds.hashing.utils.HashingUtil;
import weloveclouds.kvstore.deserialization.helper.HashRangesSetDeserializer;
import weloveclouds.kvstore.deserialization.helper.IDeserializer;
import weloveclouds.kvstore.serialization.exceptions.DeserializationException;
import weloveclouds.kvstore.serialization.helper.HashRangesSetSerializer;
import weloveclouds.kvstore.serialization.helper.ISerializer;
import weloveclouds.server.utils.SetToStringUtility;

/**
 * Tests for the {@link Set<HashRange>} to verify its serialization and deserialization processes.
 * 
 * @author Benedek
 */
public class HashRangesSetTest extends TestCase {

    private static final IDeserializer<Set<HashRange>, String> hashRangesDeserializer =
            new HashRangesSetDeserializer();
    private static final ISerializer<String, Set<HashRange>> hashRangesSerializer =
            new HashRangesSetSerializer();

    @Test
    public void testHashRangeSerializationAndDeserialization()
            throws DeserializationException, UnknownHostException {
        HashRange range1 =
                new HashRange.Builder().begin(Hash.MIN_VALUE).end(Hash.MAX_VALUE).build();
        HashRange range2 = new HashRange.Builder().begin(HashingUtil.getHash("a"))
                .end(HashingUtil.getHash("a")).build();
        Set<HashRange> hashRanges = new HashSet<>(Arrays.asList(range1, range2));

        String serializedRangess = hashRangesSerializer.serialize(hashRanges);
        Set<HashRange> deserializedRanges = hashRangesDeserializer.deserialize(serializedRangess);

        Assert.assertEquals(SetToStringUtility.toString(hashRanges),
                SetToStringUtility.toString(deserializedRanges));
        Assert.assertEquals(hashRanges, deserializedRanges);
    }

}
