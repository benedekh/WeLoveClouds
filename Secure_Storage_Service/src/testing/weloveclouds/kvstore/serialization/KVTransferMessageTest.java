package testing.weloveclouds.kvstore.serialization;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.junit.Test;

import junit.framework.Assert;
import junit.framework.TestCase;
import weloveclouds.commons.kvstore.deserialization.KVTransferMessageDeserializer;
import weloveclouds.commons.kvstore.deserialization.exceptions.DeserializationException;
import weloveclouds.commons.kvstore.models.KVEntry;
import weloveclouds.commons.kvstore.models.messages.IKVTransferMessage;
import weloveclouds.commons.kvstore.models.messages.IKVTransferMessage.StatusType;
import weloveclouds.commons.kvstore.models.messages.KVTransferMessage;
import weloveclouds.commons.kvstore.serialization.KVTransferMessageSerializer;
import weloveclouds.commons.serialization.IMessageDeserializer;
import weloveclouds.commons.serialization.IMessageSerializer;
import weloveclouds.commons.serialization.models.SerializedMessage;
import weloveclouds.commons.utils.PathUtils;
import weloveclouds.server.store.models.MovableStorageUnit;
import weloveclouds.server.store.utils.KeyWithHash;

/**
 * Tests for the {@link KVTransferMessage} to verify its serialization and deserialization
 * processes.
 * 
 * @author Benedek
 */
public class KVTransferMessageTest extends TestCase {

    private static IMessageDeserializer<IKVTransferMessage, SerializedMessage> transferMessageDeserializer =
            new KVTransferMessageDeserializer();
    private static IMessageSerializer<SerializedMessage, IKVTransferMessage> transferMessageSerializer =
            new KVTransferMessageSerializer();

    @Test
    public void testKVTransferMessageSerializationAndDeserialization()
            throws DeserializationException, IOException {
        SortedMap<KeyWithHash, String> keyval1 = new TreeMap<>();
        keyval1.put(new KeyWithHash("hello"), "world");
        keyval1.put(new KeyWithHash("apple"), "juice");
        MovableStorageUnit unit1 = new MovableStorageUnit(keyval1, PathUtils.createDummyPath());

        SortedMap<KeyWithHash, String> keyval2 = new TreeMap<>(keyval1);
        keyval2.put(new KeyWithHash("orange"), "banana");
        MovableStorageUnit unit2 = new MovableStorageUnit(keyval2, PathUtils.createDummyPath());

        Set<MovableStorageUnit> storageUnits = new HashSet<>(Arrays.asList(unit1, unit2));

        KVEntry putableEntry = new KVEntry("hello", "world");
        String removableKey = "apple";
        String responseMessage = "hello world";

        KVTransferMessage transferMessage =
                new KVTransferMessage.Builder().storageUnits(storageUnits)
                        .status(StatusType.TRANSFER_ENTRIES).putableEntry(putableEntry)
                        .removableKey(removableKey).responseMessage(responseMessage).build();

        SerializedMessage serializedMessage = transferMessageSerializer.serialize(transferMessage);
        IKVTransferMessage deserializedMessage =
                transferMessageDeserializer.deserialize(serializedMessage);

        Assert.assertEquals(transferMessage.toString(), deserializedMessage.toString());
        Assert.assertEquals(transferMessage, deserializedMessage);
    }

}
