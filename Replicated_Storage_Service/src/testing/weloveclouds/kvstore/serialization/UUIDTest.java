package testing.weloveclouds.kvstore.serialization;

import java.net.UnknownHostException;
import java.util.UUID;

import org.junit.Test;

import junit.framework.Assert;
import weloveclouds.commons.kvstore.deserialization.exceptions.DeserializationException;
import weloveclouds.commons.kvstore.deserialization.helper.UUIDDeserializer;
import weloveclouds.commons.kvstore.serialization.helper.UUIDSerializer;
import weloveclouds.commons.serialization.IDeserializer;
import weloveclouds.commons.serialization.ISerializer;
import weloveclouds.commons.serialization.models.AbstractXMLNode;

public class UUIDTest {

    private static final IDeserializer<UUID, String> uuidDeserializer = new UUIDDeserializer();
    private static final ISerializer<AbstractXMLNode, UUID> uuidSerializer = new UUIDSerializer();

    @Test
    public void testServerConnectionInfoSerializationAndDeserialization()
            throws DeserializationException, UnknownHostException {
        UUID uuid = UUID.randomUUID();

        String serializedUUID = uuidSerializer.serialize(uuid).toString();
        UUID deserializedUUID = uuidDeserializer.deserialize(serializedUUID);

        Assert.assertEquals(uuid.toString(), deserializedUUID.toString());
        Assert.assertEquals(uuid, deserializedUUID);
    }
}
