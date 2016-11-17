package testing.weloveclouds.kvstore.serialization;

import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.junit.Test;

import junit.framework.Assert;
import weloveclouds.kvstore.deserialization.helper.IDeserializer;
import weloveclouds.kvstore.deserialization.helper.MovableStorageUnitsDeserializer;
import weloveclouds.kvstore.serialization.exceptions.DeserializationException;
import weloveclouds.kvstore.serialization.helper.ISerializer;
import weloveclouds.kvstore.serialization.helper.MovableStorageUnitsSerializer;
import weloveclouds.server.store.models.MovableStorageUnit;
import weloveclouds.server.store.models.MovableStorageUnits;
import weloveclouds.server.utils.FileUtility;

public class MovableStorageUnitsTest {

    public static final IDeserializer<MovableStorageUnits, String> deserializer =
            new MovableStorageUnitsDeserializer();
    public static final ISerializer<String, MovableStorageUnits> serializer =
            new MovableStorageUnitsSerializer();

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

        String ser = serializer.serialize(storageUnits);
        MovableStorageUnits deser = deserializer.deserialize(ser);

        Assert.assertEquals(storageUnits.toString(), deser.toString());
        Assert.assertEquals(storageUnits, deser);
    }
}
