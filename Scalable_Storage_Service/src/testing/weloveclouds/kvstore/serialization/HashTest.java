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

    private static final IDeserializer<Hash, String> hashDeserializer = new HashDeserializer();
    private static final ISerializer<String, Hash> hashSerializer = new HashSerializer();

    @Test
    public void testHashSerializationAndDeserialization()
            throws DeserializationException, UnknownHostException {
        Hash hash = HashingUtil.getHash("a");

        String serializedHash = hashSerializer.serialize(hash);
        Hash deserializedHash = hashDeserializer.deserialize(serializedHash);

        Assert.assertEquals(hash.toString(), deserializedHash.toString());
        Assert.assertEquals(hash, deserializedHash);
    }

}
