package testing.weloveclouds.kvstore.serialization;

import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.junit.Test;

import junit.framework.Assert;
import weloveclouds.kvstore.deserialization.IMessageDeserializer;
import weloveclouds.kvstore.deserialization.KVTransferMessageDeserializer;
import weloveclouds.kvstore.models.messages.IKVTransferMessage.StatusType;
import weloveclouds.kvstore.models.messages.KVTransferMessage;
import weloveclouds.kvstore.serialization.IMessageSerializer;
import weloveclouds.kvstore.serialization.KVTransferMessageSerializer;
import weloveclouds.kvstore.serialization.exceptions.DeserializationException;
import weloveclouds.kvstore.serialization.models.SerializedMessage;
import weloveclouds.server.store.models.MovableStorageUnit;
import weloveclouds.server.store.models.MovableStorageUnits;
import weloveclouds.server.utils.FileUtility;

public class KVTransferMessageTest {

    private static IMessageDeserializer<KVTransferMessage, SerializedMessage> deserializer =
            new KVTransferMessageDeserializer();
    private static IMessageSerializer<SerializedMessage, KVTransferMessage> serializer =
            new KVTransferMessageSerializer();

    @Test
    public void test() throws DeserializationException, UnknownHostException {
        Map<String, String> keyval1 = new HashMap<>();
        keyval1.put("hello", "world");
        keyval1.put("apple", "juice");
        MovableStorageUnit unit1 = new MovableStorageUnit(keyval1, FileUtility.createDummyPath());

        Map<String, String> keyval2 = new HashMap<>(keyval1);
        keyval2.put("orange", "banana");
        MovableStorageUnit unit2 = new MovableStorageUnit(keyval2, FileUtility.createDummyPath());

        MovableStorageUnits storageUnits =
                new MovableStorageUnits(new HashSet<>(Arrays.asList(unit1, unit2)));

        KVTransferMessage message = new KVTransferMessage.KVTransferMessageBuilder()
                .storageUnits(storageUnits).status(StatusType.TRANSFER).build();

        SerializedMessage ser = serializer.serialize(message);
        KVTransferMessage deser = deserializer.deserialize(ser);

        Assert.assertEquals(message.toString(), deser.toString());
        Assert.assertEquals(message, deser);
    }

}
