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
import weloveclouds.commons.hashing.models.RingMetadata;
import weloveclouds.commons.hashing.models.RingMetadataPart;
import weloveclouds.commons.hashing.utils.HashingUtils;
import weloveclouds.commons.kvstore.deserialization.KVAdminMessageDeserializer;
import weloveclouds.commons.kvstore.deserialization.exceptions.DeserializationException;
import weloveclouds.commons.kvstore.models.messages.IKVAdminMessage;
import weloveclouds.commons.kvstore.models.messages.IKVAdminMessage.StatusType;
import weloveclouds.commons.kvstore.models.messages.KVAdminMessage;
import weloveclouds.commons.kvstore.serialization.KVAdminMessageSerializer;
import weloveclouds.commons.serialization.IMessageDeserializer;
import weloveclouds.commons.serialization.IMessageSerializer;
import weloveclouds.commons.serialization.models.SerializedMessage;
import weloveclouds.communication.models.ServerConnectionInfo;

/**
 * Tests for the {@link IKVAdminMessage} to verify its serialization and deserialization processes.
 * 
 * @author Benedek
 */
public class KVAdminMessageTest extends TestCase {

    private static IMessageDeserializer<IKVAdminMessage, SerializedMessage> adminMessageDeserializer =
            new KVAdminMessageDeserializer();
    private static IMessageSerializer<SerializedMessage, IKVAdminMessage> adminMessageSerializer =
            new KVAdminMessageSerializer();

    @Test
    public void testKVAdminMessageSerializationAndDeserialization()
            throws DeserializationException, UnknownHostException {
        HashRange removableRange = new HashRange.Builder().begin(HashingUtils.getHash("a"))
                .end(HashingUtils.getHash("b")).build();

        HashRange range1 =
                new HashRange.Builder().begin(Hash.MIN_VALUE).end(Hash.MAX_VALUE).build();
        HashRange writeRange = new HashRange.Builder().begin(HashingUtils.getHash("a"))
                .end(HashingUtils.getHash("a")).build();
        Set<HashRange> readRanges = new HashSet<>(Arrays.asList(range1, writeRange));
        ServerConnectionInfo connectionInfo1 =
                new ServerConnectionInfo.Builder().ipAddress("localhost").port(8080).build();
        RingMetadataPart metadataPart1 = new RingMetadataPart.Builder()
                .connectionInfo(connectionInfo1).writeRange(writeRange).build();

        ServerConnectionInfo connectionInfo2 =
                new ServerConnectionInfo.Builder().ipAddress("localhost").port(8082).build();
        RingMetadataPart metadataPart2 = new RingMetadataPart.Builder()
                .connectionInfo(connectionInfo2).readRanges(readRanges).build();

        RingMetadata metadata =
                new RingMetadata(new HashSet<>(Arrays.asList(metadataPart1, metadataPart2)));
        Set<ServerConnectionInfo> connectionInfos =
                new HashSet<>(Arrays.asList(connectionInfo1, connectionInfo2));
        String responseMessage = "hello world";

        KVAdminMessage adminMessage =
                new KVAdminMessage.Builder().status(StatusType.INITKVSERVER).ringMetadata(metadata)
                        .targetServerInfo(metadataPart1).replicaConnectionInfos(connectionInfos)
                        .removableRange(removableRange).responseMessage(responseMessage).build();

        SerializedMessage serializedMessage = adminMessageSerializer.serialize(adminMessage);
        IKVAdminMessage deserializedAdminMessage =
                adminMessageDeserializer.deserialize(serializedMessage);

        Assert.assertEquals(adminMessage.toString(), deserializedAdminMessage.toString());
        Assert.assertEquals(adminMessage, deserializedAdminMessage);
    }

}

