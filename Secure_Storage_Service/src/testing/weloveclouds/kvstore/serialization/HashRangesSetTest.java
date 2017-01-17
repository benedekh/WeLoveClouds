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
import weloveclouds.commons.hashing.utils.HashingUtils;
import weloveclouds.commons.kvstore.deserialization.exceptions.DeserializationException;
import weloveclouds.commons.kvstore.deserialization.helper.HashRangesSetDeserializer;
import weloveclouds.commons.kvstore.serialization.helper.HashRangesIterableSerializer;
import weloveclouds.commons.serialization.IDeserializer;
import weloveclouds.commons.serialization.ISerializer;
import weloveclouds.commons.serialization.models.AbstractXMLNode;
import weloveclouds.commons.utils.StringUtils;


/**
 * Tests for the {@link Set<HashRange>} to verify its serialization and deserialization processes.
 * 
 * @author Benedek
 */
public class HashRangesSetTest extends TestCase {

    private static final IDeserializer<Set<HashRange>, String> hashRangesDeserializer =
            new HashRangesSetDeserializer();
    private static final ISerializer<AbstractXMLNode, Iterable<HashRange>> hashRangesSerializer =
            new HashRangesIterableSerializer();

    @Test
    public void testHashRangeSerializationAndDeserialization()
            throws DeserializationException, UnknownHostException {
        HashRange range1 =
                new HashRange.Builder().begin(Hash.MIN_VALUE).end(Hash.MAX_VALUE).build();
        HashRange range2 = new HashRange.Builder().begin(HashingUtils.getHash("a"))
                .end(HashingUtils.getHash("a")).build();
        Set<HashRange> hashRanges = new HashSet<>(Arrays.asList(range1, range2));

        String serializedRangess = hashRangesSerializer.serialize(hashRanges).toString();
        Set<HashRange> deserializedRanges = hashRangesDeserializer.deserialize(serializedRangess);

        Assert.assertEquals(StringUtils.setToString(hashRanges),
                StringUtils.setToString(deserializedRanges));
        Assert.assertEquals(hashRanges, deserializedRanges);
    }

}
