package testing.weloveclouds.kvstore.serialization;

import java.net.UnknownHostException;

import org.junit.Test;

import junit.framework.Assert;
import weloveclouds.kvstore.deserialization.IMessageDeserializer;
import weloveclouds.kvstore.deserialization.KVAdminMessageDeserializer;
import weloveclouds.kvstore.models.messages.IKVAdminMessage.StatusType;
import weloveclouds.kvstore.models.messages.KVAdminMessage;
import weloveclouds.kvstore.serialization.IMessageSerializer;
import weloveclouds.kvstore.serialization.KVAdminMessageSerializer;
import weloveclouds.kvstore.serialization.exceptions.DeserializationException;
import weloveclouds.kvstore.serialization.models.SerializedMessage;
import weloveclouds.server.models.ServerInitializationContext;

/**
 * Tests for the {@link KVAdminMessage} to verify its serialization and deserialization processes.
 * 
 * @author Benedek
 */
public class KVAdminMessageTest {

    private static IMessageDeserializer<KVAdminMessage, SerializedMessage> adminMessageDeserializer =
            new KVAdminMessageDeserializer();
    private static IMessageSerializer<SerializedMessage, KVAdminMessage> adminMessageSerializer =
            new KVAdminMessageSerializer();

    @Test
    public void testKVAdminMessageSerializationAndDeserialization()
            throws DeserializationException, UnknownHostException {
        ServerInitializationContext context = new ServerInitializationContext.Builder()
                .cacheSize(10).displacementStrategyName("LRU").build();

        KVAdminMessage adminMessage = new KVAdminMessage.Builder().initializationContext(context)
                .status(StatusType.INITKVSERVER).build();

        SerializedMessage serializedMessage = adminMessageSerializer.serialize(adminMessage);
        KVAdminMessage deserializedAdminMessage =
                adminMessageDeserializer.deserialize(serializedMessage);

        Assert.assertEquals(adminMessage.toString(), deserializedAdminMessage.toString());
        Assert.assertEquals(adminMessage, deserializedAdminMessage);
    }

}

