package testing.weloveclouds.kvstore.serialization;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.junit.Test;

import junit.framework.Assert;
import junit.framework.TestCase;
import weloveclouds.commons.kvstore.deserialization.KVTransactionMessageDeserializer;
import weloveclouds.commons.kvstore.deserialization.exceptions.DeserializationException;
import weloveclouds.commons.kvstore.models.KVEntry;
import weloveclouds.commons.kvstore.models.messages.IKVTransactionMessage;
import weloveclouds.commons.kvstore.models.messages.IKVTransferMessage.StatusType;
import weloveclouds.commons.kvstore.models.messages.KVTransactionMessage;
import weloveclouds.commons.kvstore.models.messages.KVTransferMessage;
import weloveclouds.commons.kvstore.serialization.KVTransactionMessageSerializer;
import weloveclouds.commons.serialization.IMessageDeserializer;
import weloveclouds.commons.serialization.IMessageSerializer;
import weloveclouds.commons.serialization.models.SerializedMessage;
import weloveclouds.commons.utils.PathUtils;
import weloveclouds.server.store.models.MovableStorageUnit;

/**
 * Tests for the {@link IKVTransactionMessage} to verify its serialization and deserialization processes.
 * 
 * @author Benedek
 */
public class KVTransactionMessageTest extends TestCase {
    private static IMessageDeserializer<IKVTransactionMessage, SerializedMessage> transactionMessageDeserializer =
            new KVTransactionMessageDeserializer();
    private static IMessageSerializer<SerializedMessage, IKVTransactionMessage> transactionMessageSerializer =
            new KVTransactionMessageSerializer();

    @Test
    public void testKVTransferMessageSerializationAndDeserialization()
            throws DeserializationException, IOException {
        Map<String, String> keyval1 = new HashMap<>();
        keyval1.put("hello", "world");
        keyval1.put("apple", "juice");
        MovableStorageUnit unit1 = new MovableStorageUnit(keyval1, PathUtils.createDummyPath());

        Map<String, String> keyval2 = new HashMap<>(keyval1);
        keyval2.put("orange", "banana");
        MovableStorageUnit unit2 = new MovableStorageUnit(keyval2, PathUtils.createDummyPath());

        Set<MovableStorageUnit> storageUnits = new HashSet<>(Arrays.asList(unit1, unit2));

        KVEntry putableEntry = new KVEntry("hello", "world");
        String removableKey = "apple";
        String responseMessage = "hello world";

        KVTransferMessage transferMessage =
                new KVTransferMessage.Builder().storageUnits(storageUnits)
                        .status(StatusType.TRANSFER_ENTRIES).putableEntry(putableEntry)
                        .removableKey(removableKey).responseMessage(responseMessage).build();

        KVTransactionMessage transactionMessage =
                new KVTransactionMessage.Builder().status(IKVTransactionMessage.StatusType.INIT)
                        .transactionId(UUID.randomUUID()).transferPayload(transferMessage).build();

        SerializedMessage serializedMessage =
                transactionMessageSerializer.serialize(transactionMessage);
        IKVTransactionMessage deserializedMessage =
                transactionMessageDeserializer.deserialize(serializedMessage);

        Assert.assertEquals(transactionMessage.toString(), deserializedMessage.toString());
        Assert.assertEquals(transactionMessage, deserializedMessage);
    }
}
