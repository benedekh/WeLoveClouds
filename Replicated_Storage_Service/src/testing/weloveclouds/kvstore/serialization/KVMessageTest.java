package testing.weloveclouds.kvstore.serialization;

import org.junit.Test;

import junit.framework.Assert;
import junit.framework.TestCase;
import weloveclouds.commons.serialization.IMessageDeserializer;
import weloveclouds.kvstore.deserialization.KVMessageDeserializer;
import weloveclouds.kvstore.models.messages.IKVMessage.StatusType;
import weloveclouds.kvstore.models.messages.KVMessage;
import weloveclouds.commons.serialization.IMessageSerializer;
import weloveclouds.kvstore.serialization.KVMessageSerializer;
import weloveclouds.kvstore.serialization.exceptions.DeserializationException;
import weloveclouds.kvstore.serialization.models.SerializedMessage;

/**
 * Tests for the {@link KVMessage} to verify its serialization and deserialization processes.
 * 
 * @author Benedek
 */
public class KVMessageTest extends TestCase  {

    private static IMessageDeserializer<KVMessage, SerializedMessage> messageDeserializer =
            new KVMessageDeserializer();
    private static IMessageSerializer<SerializedMessage, KVMessage> messageSerializer =
            new KVMessageSerializer();

    @Test
    public void testKVMessageSerializationAndDeserialization() throws DeserializationException {
        KVMessage kvMessage =
                new KVMessage.Builder().key("hello").value("world").status(StatusType.PUT).build();

        SerializedMessage serializedMessage = messageSerializer.serialize(kvMessage);
        KVMessage deserializedMessage = messageDeserializer.deserialize(serializedMessage);

        Assert.assertEquals(kvMessage.toString(), deserializedMessage.toString());
        Assert.assertEquals(kvMessage, deserializedMessage);
    }

}
