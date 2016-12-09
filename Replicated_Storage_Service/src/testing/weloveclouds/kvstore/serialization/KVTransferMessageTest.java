package testing.weloveclouds.kvstore.serialization;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.junit.Test;

import junit.framework.Assert;
import junit.framework.TestCase;
import weloveclouds.kvstore.deserialization.IMessageDeserializer;
import weloveclouds.kvstore.deserialization.KVTransferMessageDeserializer;
import weloveclouds.kvstore.models.KVEntry;
import weloveclouds.kvstore.models.messages.IKVTransferMessage.StatusType;
import weloveclouds.kvstore.models.messages.KVTransferMessage;
import weloveclouds.kvstore.serialization.IMessageSerializer;
import weloveclouds.kvstore.serialization.KVTransferMessageSerializer;
import weloveclouds.kvstore.serialization.exceptions.DeserializationException;
import weloveclouds.kvstore.serialization.models.SerializedMessage;
import weloveclouds.server.store.models.MovableStorageUnit;
import weloveclouds.server.store.models.MovableStorageUnits;
import weloveclouds.server.utils.FileUtility;

/**
 * Tests for the {@link KVTransferMessage} to verify its serialization and deserialization
 * processes.
 * 
 * @author Benedek
 */
public class KVTransferMessageTest extends TestCase {

    private static IMessageDeserializer<KVTransferMessage, SerializedMessage> transferMessageDeserializer =
            new KVTransferMessageDeserializer();
    private static IMessageSerializer<SerializedMessage, KVTransferMessage> transferMessageSerializer =
            new KVTransferMessageSerializer();

    @Test
    public void testKVTransferMessageSerializationAndDeserialization()
            throws DeserializationException, IOException {
        Map<String, String> keyval1 = new HashMap<>();
        keyval1.put("hello", "world");
        keyval1.put("apple", "juice");
        MovableStorageUnit unit1 = new MovableStorageUnit(keyval1, FileUtility.createDummyPath());

        Map<String, String> keyval2 = new HashMap<>(keyval1);
        keyval2.put("orange", "banana");
        MovableStorageUnit unit2 = new MovableStorageUnit(keyval2, FileUtility.createDummyPath());

        MovableStorageUnits storageUnits =
                new MovableStorageUnits(new HashSet<>(Arrays.asList(unit1, unit2)));

        KVEntry putableEntry = new KVEntry("hello", "world");
        String removableKey = "apple";
        String responseMessage = "hello world";

        KVTransferMessage transferMessage =
                new KVTransferMessage.Builder().storageUnits(storageUnits)
                        .status(StatusType.TRANSFER_ENTRIES).putableEntry(putableEntry)
                        .removableKey(removableKey).responseMessage(responseMessage).build();

        SerializedMessage serializedMessage = transferMessageSerializer.serialize(transferMessage);
        KVTransferMessage deserializedMessage =
                transferMessageDeserializer.deserialize(serializedMessage);

        Assert.assertEquals(transferMessage.toString(), deserializedMessage.toString());
        Assert.assertEquals(transferMessage, deserializedMessage);
    }

}
