package testing.weloveclouds.kvstore.serialization;

import java.net.UnknownHostException;

import org.junit.Test;

import junit.framework.Assert;
import junit.framework.TestCase;
import weloveclouds.hashing.models.Hash;
import weloveclouds.hashing.models.HashRange;
import weloveclouds.kvstore.deserialization.helper.HashRangeWithRoleDeserializer;
import weloveclouds.kvstore.deserialization.helper.IDeserializer;
import weloveclouds.kvstore.serialization.exceptions.DeserializationException;
import weloveclouds.kvstore.serialization.helper.HashRangeWithRoleSerializer;
import weloveclouds.kvstore.serialization.helper.ISerializer;
import weloveclouds.server.models.replication.HashRangeWithRole;
import weloveclouds.server.models.replication.Role;

/**
 * Tests for the {@link HashRangeWithRole} to verify its serialization and deserialization
 * processes.
 * 
 * @author Benedek
 */
public class HashRangeWithRoleTest extends TestCase {

    private static final IDeserializer<HashRangeWithRole, String> hashRangeWithRoleDeserializer =
            new HashRangeWithRoleDeserializer();
    private static final ISerializer<String, HashRangeWithRole> hashRangeWithRoleSerializer =
            new HashRangeWithRoleSerializer();

    @Test
    public void testHashRangeSerializationAndDeserialization()
            throws DeserializationException, UnknownHostException {
        HashRangeWithRole hashRangeWithRole = new HashRangeWithRole.Builder()
                .hashRange(
                        new HashRange.Builder().begin(Hash.MIN_VALUE).end(Hash.MAX_VALUE).build())
                .role(Role.MASTER).build();

        String serializedRangeWithRole = hashRangeWithRoleSerializer.serialize(hashRangeWithRole);
        HashRangeWithRole deserializedRangeWithRole =
                hashRangeWithRoleDeserializer.deserialize(serializedRangeWithRole);

        Assert.assertEquals(hashRangeWithRole.toString(), deserializedRangeWithRole.toString());
        Assert.assertEquals(hashRangeWithRole, deserializedRangeWithRole);
    }

}
