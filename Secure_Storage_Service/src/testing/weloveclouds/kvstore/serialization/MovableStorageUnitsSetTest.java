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
import weloveclouds.commons.kvstore.deserialization.helper.MovableStorageUnitsSetDeserializer;
import weloveclouds.commons.kvstore.serialization.helper.MovableStorageUnitsIterableSerializer;
import weloveclouds.commons.serialization.IDeserializer;
import weloveclouds.commons.serialization.ISerializer;
import weloveclouds.commons.serialization.models.AbstractXMLNode;
import weloveclouds.commons.utils.PathUtils;
import weloveclouds.commons.utils.StringUtils;
import weloveclouds.server.store.models.MovableStorageUnit;
import weloveclouds.server.store.utils.KeyWithHash;

/**
 * Tests for the {@link Set<MovableStorageUnit>} to verify its serialization and deserialization
 * processes.
 * 
 * @author Benedek
 */
public class MovableStorageUnitsSetTest extends TestCase {

    private static final IDeserializer<Set<MovableStorageUnit>, String> storageUnitsDeserializer =
            new MovableStorageUnitsSetDeserializer();
    private static final ISerializer<AbstractXMLNode, Iterable<MovableStorageUnit>> storageUnitsSerializer =
            new MovableStorageUnitsIterableSerializer();

    @Test
    public void testMovableStorageUnitsSerializationAndDeserialization()
            throws DeserializationException, IOException {
        SortedMap<KeyWithHash, String> keyval1 = new TreeMap<>();
        keyval1.put(new KeyWithHash("hello"), "world");
        keyval1.put(new KeyWithHash("apple"), "juice");
        MovableStorageUnit unit1 = new MovableStorageUnit(keyval1, PathUtils.createDummyPath());

        SortedMap<KeyWithHash, String> keyval2 = new TreeMap<>(keyval1);
        keyval2.put(new KeyWithHash("orange"), "banana");
        MovableStorageUnit unit2 = new MovableStorageUnit(keyval2, PathUtils.createDummyPath());

        Set<MovableStorageUnit> storageUnits = new HashSet<>(Arrays.asList(unit1, unit2));

        String serializedStorageUnits = storageUnitsSerializer.serialize(storageUnits).toString();
        Set<MovableStorageUnit> deserializedStorageUnits =
                storageUnitsDeserializer.deserialize(serializedStorageUnits);

        Assert.assertEquals(StringUtils.setToString(storageUnits),
                StringUtils.setToString(deserializedStorageUnits));
        Assert.assertEquals(storageUnits, deserializedStorageUnits);
    }
}
