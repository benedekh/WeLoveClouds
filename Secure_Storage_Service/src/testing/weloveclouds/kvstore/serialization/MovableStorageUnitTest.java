package testing.weloveclouds.kvstore.serialization;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import junit.framework.Assert;
import junit.framework.TestCase;
import weloveclouds.commons.kvstore.deserialization.exceptions.DeserializationException;
import weloveclouds.commons.kvstore.deserialization.helper.MovableStorageUnitDeserializer;
import weloveclouds.commons.kvstore.serialization.helper.MovableStorageUnitSerializer;
import weloveclouds.commons.serialization.IDeserializer;
import weloveclouds.commons.serialization.ISerializer;
import weloveclouds.commons.serialization.models.AbstractXMLNode;
import weloveclouds.commons.utils.PathUtils;
import weloveclouds.server.store.models.MovableStorageUnit;

/**
 * Tests for the {@link MovableStorageUnit} to verify its serialization and deserialization
 * processes.
 * 
 * @author Benedek
 */
public class MovableStorageUnitTest extends TestCase {

    private static final IDeserializer<MovableStorageUnit, String> storageUnitDeserializer =
            new MovableStorageUnitDeserializer();
    private static final ISerializer<AbstractXMLNode, MovableStorageUnit> storageUnitSerializer =
            new MovableStorageUnitSerializer();

    @Test
    public void testMovableStorageUnitSerializationAndDeserialization()
            throws DeserializationException, IOException {
        Map<String, String> keyval = new HashMap<>();
        keyval.put("hello", "world");
        keyval.put("apple", "juice");

        MovableStorageUnit storageUnit =
                new MovableStorageUnit(keyval, PathUtils.createDummyPath());

        String serializedStorageUnit = storageUnitSerializer.serialize(storageUnit).toString();
        MovableStorageUnit deserializedStorageUnit =
                storageUnitDeserializer.deserialize(serializedStorageUnit);

        Assert.assertEquals(storageUnit.toString(), deserializedStorageUnit.toString());
        Assert.assertEquals(storageUnit, deserializedStorageUnit);
    }
}
