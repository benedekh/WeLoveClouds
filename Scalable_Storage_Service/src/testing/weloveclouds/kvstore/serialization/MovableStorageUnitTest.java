package testing.weloveclouds.kvstore.serialization;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import junit.framework.Assert;
import weloveclouds.kvstore.deserialization.helper.IDeserializer;
import weloveclouds.kvstore.deserialization.helper.MovableStorageUnitDeserializer;
import weloveclouds.kvstore.serialization.exceptions.DeserializationException;
import weloveclouds.kvstore.serialization.helper.ISerializer;
import weloveclouds.kvstore.serialization.helper.MovableStorageUnitSerializer;
import weloveclouds.server.store.models.MovableStorageUnit;
import weloveclouds.server.utils.FileUtility;

public class MovableStorageUnitTest {

    private static final IDeserializer<MovableStorageUnit, String> storageUnitDeserializer =
            new MovableStorageUnitDeserializer();
    private static final ISerializer<String, MovableStorageUnit> storageUnitSerializer =
            new MovableStorageUnitSerializer();

    @Test
    public void testMovableStorageUnitSerializationAndDeserialization()
            throws DeserializationException, UnknownHostException {
        Map<String, String> keyval = new HashMap<>();
        keyval.put("hello", "world");
        keyval.put("apple", "juice");

        MovableStorageUnit storageUnit =
                new MovableStorageUnit(keyval, FileUtility.createDummyPath());

        String serializedStorageUnit = storageUnitSerializer.serialize(storageUnit);
        MovableStorageUnit deserializedStorageUnit =
                storageUnitDeserializer.deserialize(serializedStorageUnit);

        Assert.assertEquals(storageUnit.toString(), deserializedStorageUnit.toString());
        Assert.assertEquals(storageUnit, deserializedStorageUnit);
    }
}
