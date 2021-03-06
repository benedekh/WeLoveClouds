package testing.weloveclouds.kvstore.serialization;

import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.junit.Test;

import junit.framework.Assert;
import junit.framework.TestCase;
import weloveclouds.commons.kvstore.deserialization.helper.IDeserializer;
import weloveclouds.commons.kvstore.deserialization.helper.MovableStorageUnitsDeserializer;
import weloveclouds.commons.kvstore.deserialization.exceptions.DeserializationException;
import weloveclouds.commons.kvstore.serialization.helper.ISerializer;
import weloveclouds.commons.kvstore.serialization.helper.MovableStorageUnitsSerializer;
import weloveclouds.server.store.models.MovableStorageUnit;
import weloveclouds.server.store.models.MovableStorageUnits;
import weloveclouds.server.utils.FileUtility;

/**
 * Tests for the {@link MovableStorageUnits} to verify its serialization and deserialization
 * processes.
 * 
 * @author Benedek
 */
public class MovableStorageUnitsTest extends TestCase {

    private static final IDeserializer<MovableStorageUnits, String> storageUnitsDeserializer =
            new MovableStorageUnitsDeserializer();
    private static final ISerializer<String, MovableStorageUnits> storageUnitsSerializer =
            new MovableStorageUnitsSerializer();

    @Test
    public void testMovableStorageUnitsSerializationAndDeserialization()
            throws DeserializationException, UnknownHostException {
        Map<String, String> keyval1 = new HashMap<>();
        keyval1.put("hello", "world");
        keyval1.put("apple", "juice");
        MovableStorageUnit unit1 = new MovableStorageUnit(keyval1, FileUtility.createDummyPath());

        Map<String, String> keyval2 = new HashMap<>(keyval1);
        keyval2.put("orange", "banana");
        MovableStorageUnit unit2 = new MovableStorageUnit(keyval2, FileUtility.createDummyPath());

        MovableStorageUnits storageUnits =
                new MovableStorageUnits(new HashSet<>(Arrays.asList(unit1, unit2)));

        String serializedStorageUnits = storageUnitsSerializer.serialize(storageUnits);
        MovableStorageUnits deserializedStorageUnits =
                storageUnitsDeserializer.deserialize(serializedStorageUnits);

        Assert.assertEquals(storageUnits.toString(), deserializedStorageUnits.toString());
        Assert.assertEquals(storageUnits, deserializedStorageUnits);
    }
}
