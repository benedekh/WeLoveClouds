package testing.weloveclouds.kvstore.serialization;

import java.net.UnknownHostException;

import org.junit.Test;

import junit.framework.Assert;
import weloveclouds.hashing.models.Hash;
import weloveclouds.hashing.utils.HashingUtil;
import weloveclouds.kvstore.deserialization.helper.HashDeserializer;
import weloveclouds.kvstore.deserialization.helper.IDeserializer;
import weloveclouds.kvstore.serialization.exceptions.DeserializationException;
import weloveclouds.kvstore.serialization.helper.HashSerializer;
import weloveclouds.kvstore.serialization.helper.ISerializer;

public class HashTest {

    public static final IDeserializer<Hash, String> deserializer = new HashDeserializer();
    public static final ISerializer<String, Hash> serializer = new HashSerializer();

    @Test
    public void test() throws DeserializationException, UnknownHostException {
        Hash hash = HashingUtil.getHash("a");

        String ser = serializer.serialize(hash);
        Hash deser = deserializer.deserialize(ser);

        Assert.assertEquals(hash.toString(), deser.toString());
        Assert.assertEquals(hash, deser);
    }

}
