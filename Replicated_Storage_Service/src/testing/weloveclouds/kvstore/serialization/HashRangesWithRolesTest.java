package testing.weloveclouds.kvstore.serialization;


import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashSet;

import org.junit.Test;

import junit.framework.Assert;
import junit.framework.TestCase;
import weloveclouds.hashing.models.Hash;
import weloveclouds.hashing.models.HashRange;
import weloveclouds.hashing.utils.HashingUtil;
import weloveclouds.kvstore.deserialization.helper.HashRangesWithRolesDeserializer;
import weloveclouds.kvstore.deserialization.helper.IDeserializer;
import weloveclouds.kvstore.serialization.exceptions.DeserializationException;
import weloveclouds.kvstore.serialization.helper.HashRangesWithRolesSerializer;
import weloveclouds.kvstore.serialization.helper.ISerializer;
import weloveclouds.server.models.replication.HashRangeWithRole;
import weloveclouds.server.models.replication.HashRangesWithRoles;
import weloveclouds.server.models.replication.Role;

/**
 * Tests for the {@link HashRangesWithRoles} to verify its serialization and deserialization
 * processes.
 * 
 * @author Benedek
 */
public class HashRangesWithRolesTest extends TestCase {

    private static final IDeserializer<HashRangesWithRoles, String> hashRangesWithRolesDeserializer =
            new HashRangesWithRolesDeserializer();
    private static final ISerializer<String, HashRangesWithRoles> hashRangesWithRolesSerializer =
            new HashRangesWithRolesSerializer();

    @Test
    public void testHashRangeSerializationAndDeserialization()
            throws DeserializationException, UnknownHostException {
        HashRangeWithRole hashRangeWithRole1 = new HashRangeWithRole.Builder()
                .hashRange(
                        new HashRange.Builder().begin(Hash.MIN_VALUE).end(Hash.MAX_VALUE).build())
                .role(Role.COORDINATOR).build();
        HashRangeWithRole hashRangeWithRole2 = new HashRangeWithRole.Builder()
                .hashRange(new HashRange.Builder().begin(HashingUtil.getHash("a"))
                        .end(HashingUtil.getHash("a")).build())
                .role(Role.REPLICA).build();

        HashRangesWithRoles hashRangesWithRoles = new HashRangesWithRoles(
                new HashSet<>(Arrays.asList(hashRangeWithRole1, hashRangeWithRole2)));

        String serializedRangesWithRoles =
                hashRangesWithRolesSerializer.serialize(hashRangesWithRoles);
        HashRangesWithRoles deserializedRangesWithRoles =
                hashRangesWithRolesDeserializer.deserialize(serializedRangesWithRoles);

        Assert.assertEquals(hashRangesWithRoles.toString(), deserializedRangesWithRoles.toString());
        Assert.assertEquals(hashRangesWithRoles, deserializedRangesWithRoles);
    }

}
