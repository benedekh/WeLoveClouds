package testing.weloveclouds.kvstore.serialization;

import java.net.UnknownHostException;

import org.junit.Test;

import junit.framework.Assert;
import weloveclouds.hashing.models.Hash;
import weloveclouds.hashing.models.HashRange;
import weloveclouds.hashing.utils.HashingUtil;
import weloveclouds.kvstore.deserialization.helper.HashRangeDeserializer;
import weloveclouds.kvstore.deserialization.helper.IDeserializer;
import weloveclouds.kvstore.serialization.exceptions.DeserializationException;
import weloveclouds.kvstore.serialization.helper.HashRangeSerializer;
import weloveclouds.kvstore.serialization.helper.ISerializer;

public class HashRangeTest {

    public static final IDeserializer<HashRange, String> deserializer = new HashRangeDeserializer();
    public static final ISerializer<String, HashRange> serializer = new HashRangeSerializer();

    @Test
    public void test() throws DeserializationException, UnknownHostException {
        Hash start = HashingUtil.getHash("a");
        Hash end = HashingUtil.getHash("z");
        HashRange range = new HashRange(start, end);

        String ser = serializer.serialize(range);
        HashRange deser = deserializer.deserialize(ser);

        Assert.assertEquals(range.toString(), deser.toString());
        Assert.assertEquals(range, deser);
    }

}
