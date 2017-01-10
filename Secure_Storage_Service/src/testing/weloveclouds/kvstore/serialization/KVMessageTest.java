package testing.weloveclouds.kvstore.serialization;

import org.junit.Test;

import junit.framework.Assert;
import junit.framework.TestCase;
import weloveclouds.commons.kvstore.deserialization.KVMessageDeserializer;
import weloveclouds.commons.kvstore.deserialization.exceptions.DeserializationException;
import weloveclouds.commons.kvstore.models.messages.IKVMessage;
import weloveclouds.commons.kvstore.models.messages.IKVMessage.StatusType;
import weloveclouds.commons.kvstore.models.messages.KVMessage;
import weloveclouds.commons.kvstore.serialization.KVMessageSerializer;
import weloveclouds.commons.serialization.IMessageDeserializer;
import weloveclouds.commons.serialization.IMessageSerializer;
import weloveclouds.commons.serialization.models.SerializedMessage;

/**
 * Tests for the {@link IKVMessage} to verify its serialization and deserialization processes.
 * 
 * @author Benedek
 */
public class KVMessageTest extends TestCase {

    private static IMessageDeserializer<IKVMessage, SerializedMessage> messageDeserializer =
            new KVMessageDeserializer();
    private static IMessageSerializer<SerializedMessage, IKVMessage> messageSerializer =
            new KVMessageSerializer();

    @Test
    public void testKVMessageSerializationAndDeserialization() throws DeserializationException {
        KVMessage kvMessage =
                new KVMessage.Builder().key("hello").value("world").status(StatusType.PUT).build();

        SerializedMessage serializedMessage = messageSerializer.serialize(kvMessage);
        IKVMessage deserializedMessage = messageDeserializer.deserialize(serializedMessage);

        Assert.assertEquals(kvMessage.toString(), deserializedMessage.toString());
        Assert.assertEquals(kvMessage, deserializedMessage);
    }

}
