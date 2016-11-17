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

    public static final IDeserializer<MovableStorageUnit, String> deserializer =
            new MovableStorageUnitDeserializer();
    public static final ISerializer<String, MovableStorageUnit> serializer =
            new MovableStorageUnitSerializer();

    @Test
    public void test() throws DeserializationException, UnknownHostException {
        Map<String, String> keyval = new HashMap<>();
        keyval.put("hello", "world");
        keyval.put("apple", "juice");
        
        MovableStorageUnit unit = new MovableStorageUnit(keyval, FileUtility.createDummyPath());

        String ser = serializer.serialize(unit);
        MovableStorageUnit deser = deserializer.deserialize(ser);

        Assert.assertEquals(unit.toString(), deser.toString());
        Assert.assertEquals(unit, deser);
    }
}
