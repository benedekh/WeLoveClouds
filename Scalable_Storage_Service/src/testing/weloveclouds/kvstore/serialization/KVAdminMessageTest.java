package testing.weloveclouds.kvstore.serialization;

import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashSet;

import org.junit.Test;

import junit.framework.Assert;
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
import weloveclouds.server.models.ServerInitializationContext;

public class KVAdminMessageTest {

    private static IMessageDeserializer<KVAdminMessage, SerializedMessage> deserializer =
            new KVAdminMessageDeserializer();
    private static IMessageSerializer<SerializedMessage, KVAdminMessage> serializer =
            new KVAdminMessageSerializer();

    @Test
    public void test() throws DeserializationException, UnknownHostException {
        RingMetadataPart metadataPart = new RingMetadataPart.RingMetadataPartBuilder()
                .connectionInfo(new ServerConnectionInfo.ServerConnectionInfoBuilder()
                        .ipAddress("localhost").port(8080).build())
                .range(new HashRange(HashingUtil.getHash("a"), HashingUtil.getHash("b"))).build();
        RingMetadata metadata = new RingMetadata(new HashSet<>(Arrays.asList(metadataPart,
                new RingMetadataPart.RingMetadataPartBuilder()
                        .connectionInfo(new ServerConnectionInfo.ServerConnectionInfoBuilder()
                                .ipAddress("localhost").port(8082).build())
                        .range(new HashRange(Hash.MIN_VALUE, Hash.MAX_VALUE)).build())));
        ServerInitializationContext context = new ServerInitializationContext(metadata, 10, "LRU");

        KVAdminMessage message = new KVAdminMessage.KVAdminMessageBuilder()
                .initializationContext(context).status(StatusType.INITKVSERVER).build();

        SerializedMessage ser = serializer.serialize(message);
        KVAdminMessage deser = deserializer.deserialize(ser);
        
        Assert.assertEquals(message.toString(), deser.toString());
        Assert.assertEquals(message, deser);
    }

}

