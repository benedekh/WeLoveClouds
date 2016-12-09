package testing.weloveclouds.kvstore.serialization;

import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashSet;

import org.junit.Test;

import junit.framework.Assert;
import junit.framework.TestCase;
import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.communication.models.ServerConnectionInfos;
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
        HashRange removableRange = new HashRange.Builder().begin(HashingUtil.getHash("a"))
                .end(HashingUtil.getHash("b")).build();

        HashRangeWithRole hashRangeWithRole1 = new HashRangeWithRole.Builder()
                .hashRange(removableRange).role(Role.COORDINATOR).build();
        ServerConnectionInfo connectionInfo1 =
                new ServerConnectionInfo.Builder().ipAddress("localhost").port(8080).build();
        RingMetadataPart metadataPart1 = new RingMetadataPart.Builder()
                .connectionInfo(connectionInfo1).rangeWithRole(hashRangeWithRole1).build();

        HashRangeWithRole hashRangeWithRole2 = new HashRangeWithRole.Builder()
                .hashRange(
                        new HashRange.Builder().begin(Hash.MIN_VALUE).end(Hash.MAX_VALUE).build())
                .role(Role.COORDINATOR).build();
        ServerConnectionInfo connectionInfo2 =
                new ServerConnectionInfo.Builder().ipAddress("localhost").port(8082).build();
        RingMetadataPart metadataPart2 = new RingMetadataPart.Builder()
                .connectionInfo(connectionInfo2).rangeWithRole(hashRangeWithRole2).build();

        RingMetadata metadata =
                new RingMetadata(new HashSet<>(Arrays.asList(metadataPart1, metadataPart2)));
        HashRangesWithRoles rangesWithRoles = new HashRangesWithRoles(
                new HashSet<>(Arrays.asList(hashRangeWithRole1, hashRangeWithRole2)));
        ServerConnectionInfos connectionInfos = new ServerConnectionInfos(
                new HashSet<>(Arrays.asList(connectionInfo1, connectionInfo2)));
        String responseMessage = "hello world";

        KVAdminMessage adminMessage = new KVAdminMessage.Builder().status(StatusType.INITKVSERVER)
                .ringMetadata(metadata).targetServerInfo(metadataPart1)
                .rangesWithRoles(rangesWithRoles).replicaConnectionInfos(connectionInfos)
                .removableRange(removableRange).responseMessage(responseMessage).build();

        SerializedMessage serializedMessage = adminMessageSerializer.serialize(adminMessage);
        KVAdminMessage deserializedAdminMessage =
                adminMessageDeserializer.deserialize(serializedMessage);

        Assert.assertEquals(adminMessage.toString(), deserializedAdminMessage.toString());
        Assert.assertEquals(adminMessage, deserializedAdminMessage);
    }

}

