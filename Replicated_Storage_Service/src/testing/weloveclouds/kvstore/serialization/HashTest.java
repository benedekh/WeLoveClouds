package testing.weloveclouds.kvstore.serialization;

import java.net.UnknownHostException;

import org.junit.Test;

import junit.framework.Assert;
import junit.framework.TestCase;
import weloveclouds.commons.hashing.models.Hash;
import weloveclouds.commons.hashing.utils.HashingUtils;
import weloveclouds.commons.kvstore.deserialization.helper.HashDeserializer;
import weloveclouds.commons.kvstore.deserialization.exceptions.DeserializationException;
import weloveclouds.commons.kvstore.serialization.helper.HashSerializer;
import weloveclouds.commons.serialization.IDeserializer;
import weloveclouds.commons.serialization.ISerializer;

/**
 * Tests for the {@link Hash} to verify its serialization and deserialization processes.
 * 
 * @author Benedek
 */
public class HashTest extends TestCase {

    private static final IDeserializer<Hash, String> hashDeserializer = new HashDeserializer();
    private static final ISerializer<String, Hash> hashSerializer = new HashSerializer();

    @Test
    public void testHashSerializationAndDeserialization()
            throws DeserializationException, UnknownHostException {
        Hash hash = HashingUtils.getHash("a");

        String serializedHash = hashSerializer.serialize(hash);
        Hash deserializedHash = hashDeserializer.deserialize(serializedHash);

        Assert.assertEquals(hash.toString(), deserializedHash.toString());
        Assert.assertEquals(hash, deserializedHash);
    }

}
