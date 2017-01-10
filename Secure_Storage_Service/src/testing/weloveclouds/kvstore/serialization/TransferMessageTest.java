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
import weloveclouds.commons.kvstore.deserialization.exceptions.DeserializationException;
import weloveclouds.commons.kvstore.deserialization.helper.TransferMessageDeserializer;
import weloveclouds.commons.kvstore.models.KVEntry;
import weloveclouds.commons.kvstore.models.messages.IKVTransferMessage;
import weloveclouds.commons.kvstore.models.messages.IKVTransferMessage.StatusType;
import weloveclouds.commons.kvstore.models.messages.KVTransferMessage;
import weloveclouds.commons.kvstore.serialization.helper.TransferMessageSerializer;
import weloveclouds.commons.serialization.IDeserializer;
import weloveclouds.commons.serialization.ISerializer;
import weloveclouds.commons.serialization.models.AbstractXMLNode;
import weloveclouds.commons.utils.PathUtils;
import weloveclouds.server.store.models.MovableStorageUnit;
import weloveclouds.server.store.utils.KeyWithHash;


/**
 * Tests for the {@link IKVTransferMessage} to verify its serialization and deserialization
 * processes.
 * 
 * @author Benedek
 */
public class TransferMessageTest extends TestCase {

    private static IDeserializer<IKVTransferMessage, String> transferMessageDeserializer =
            new TransferMessageDeserializer();
    private static ISerializer<AbstractXMLNode, IKVTransferMessage> transferMessageSerializer =
            new TransferMessageSerializer();

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

        String serializedMessage = transferMessageSerializer.serialize(transferMessage).toString();
        IKVTransferMessage deserializedMessage =
                transferMessageDeserializer.deserialize(serializedMessage);

        Assert.assertEquals(transferMessage.toString(), deserializedMessage.toString());
        Assert.assertEquals(transferMessage, deserializedMessage);
    }
}
