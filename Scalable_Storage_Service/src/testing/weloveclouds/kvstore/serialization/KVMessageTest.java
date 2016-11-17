package testing.weloveclouds.kvstore.serialization;

import org.junit.Test;

import junit.framework.Assert;
import weloveclouds.kvstore.deserialization.IMessageDeserializer;
import weloveclouds.kvstore.deserialization.KVMessageDeserializer;
import weloveclouds.kvstore.models.messages.IKVMessage.StatusType;
import weloveclouds.kvstore.models.messages.KVMessage;
import weloveclouds.kvstore.serialization.IMessageSerializer;
import weloveclouds.kvstore.serialization.KVMessageSerializer;
import weloveclouds.kvstore.serialization.exceptions.DeserializationException;
import weloveclouds.kvstore.serialization.models.SerializedMessage;

public class KVMessageTest {

    private static IMessageDeserializer<KVMessage, SerializedMessage> deserializer =
            new KVMessageDeserializer();
    private static IMessageSerializer<SerializedMessage, KVMessage> serializer =
            new KVMessageSerializer();

    @Test
    public void test() throws DeserializationException {
        KVMessage message = new KVMessage.KVMessageBuilder().key("hello").value("world")
                .status(StatusType.PUT).build();

        SerializedMessage ser = serializer.serialize(message);
        KVMessage deser = deserializer.deserialize(ser);

        Assert.assertEquals(message.toString(), deser.toString());
        Assert.assertEquals(message, deser);
    }

}
