package testing.weloveclouds.kvstore.serialization;

import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashSet;

import org.junit.Test;

import junit.framework.Assert;
import junit.framework.TestCase;
import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.hashing.models.Hash;
import weloveclouds.hashing.models.HashRange;
import weloveclouds.hashing.models.RingMetadata;
import weloveclouds.hashing.models.RingMetadataPart;
import weloveclouds.hashing.utils.HashingUtil;
import weloveclouds.kvstore.deserialization.IMessageDeserializer;
import weloveclouds.kvstore.deserialization.KVAdminMessageDeserializer;
import weloveclouds.kvstore.models.messages.IKVAdminMessage.StatusType;
import weloveclouds.kvstore.models.messages.KVAdminMessage;
import weloveclouds.kvstore.serialization.IMessageSerializer;
import weloveclouds.kvstore.serialization.KVAdminMessageSerializer;
import weloveclouds.kvstore.serialization.exceptions.DeserializationException;
import weloveclouds.kvstore.serialization.models.SerializedMessage;
import weloveclouds.server.models.replication.HashRangeWithRole;
import weloveclouds.server.models.replication.HashRangesWithRoles;
import weloveclouds.server.models.replication.Role;

/**
 * Tests for the {@link KVAdminMessage} to verify its serialization and deserialization processes.
 * 
 * @author Benedek
 */
public class KVAdminMessageTest extends TestCase {

    private static IMessageDeserializer<KVAdminMessage, SerializedMessage> adminMessageDeserializer =
            new KVAdminMessageDeserializer();
    private static IMessageSerializer<SerializedMessage, KVAdminMessage> adminMessageSerializer =
            new KVAdminMessageSerializer();

    @Test
    public void testKVAdminMessageSerializationAndDeserialization()
            throws DeserializationException, UnknownHostException {
        HashRangeWithRole hashRangeWithRole1 = new HashRangeWithRole.Builder()
                .hashRange(new HashRange.Builder().begin(HashingUtil.getHash("a"))
                        .end(HashingUtil.getHash("b")).build())
                .role(Role.MASTER).build();
        RingMetadataPart metadataPart1 = new RingMetadataPart.Builder().connectionInfo(
                new ServerConnectionInfo.Builder().ipAddress("localhost").port(8080).build())
                .rangeWithRole(hashRangeWithRole1).build();

        HashRangeWithRole hashRangeWithRole2 = new HashRangeWithRole.Builder()
                .hashRange(
                        new HashRange.Builder().begin(Hash.MIN_VALUE).end(Hash.MAX_VALUE).build())
                .role(Role.MASTER).build();
        RingMetadataPart metadataPart2 = new RingMetadataPart.Builder().connectionInfo(
                new ServerConnectionInfo.Builder().ipAddress("localhost").port(8082).build())
                .rangeWithRole(hashRangeWithRole2).build();

        RingMetadata metadata =
                new RingMetadata(new HashSet<>(Arrays.asList(metadataPart1, metadataPart2)));
        HashRangesWithRoles rangesWithRoles = new HashRangesWithRoles(
                new HashSet<>(Arrays.asList(hashRangeWithRole1, hashRangeWithRole2)));

        KVAdminMessage adminMessage = new KVAdminMessage.Builder().status(StatusType.INITKVSERVER)
                .ringMetadata(metadata).targetServerInfo(metadataPart1)
                .rangesWithRoles(rangesWithRoles).build();

        SerializedMessage serializedMessage = adminMessageSerializer.serialize(adminMessage);
        KVAdminMessage deserializedAdminMessage =
                adminMessageDeserializer.deserialize(serializedMessage);

        Assert.assertEquals(adminMessage.toString(), deserializedAdminMessage.toString());
        Assert.assertEquals(adminMessage, deserializedAdminMessage);
    }

}

