package testing.weloveclouds.kvstore.serialization;

import java.net.UnknownHostException;

import org.junit.Test;

import junit.framework.Assert;
import junit.framework.TestCase;
import weloveclouds.commons.kvstore.deserialization.exceptions.DeserializationException;
import weloveclouds.commons.kvstore.deserialization.helper.KVEntryDeserializer;
import weloveclouds.commons.kvstore.models.KVEntry;
import weloveclouds.commons.kvstore.serialization.helper.KVEntrySerializer;
import weloveclouds.commons.serialization.IDeserializer;
import weloveclouds.commons.serialization.ISerializer;
import weloveclouds.commons.serialization.models.AbstractXMLNode;

/**
 * Tests for the {@link KVEntry} to verify its serialization and deserialization processes.
 * 
 * @author Benedek
 */
public class KVEntryTest extends TestCase {

    private static final IDeserializer<KVEntry, String> kvEntryDeserializer =
            new KVEntryDeserializer();
    private static final ISerializer<AbstractXMLNode, KVEntry> kvEntrySerializer =
            new KVEntrySerializer();

    @Test
    public void testHashSerializationAndDeserialization()
            throws DeserializationException, UnknownHostException {
        KVEntry kvEntry = new KVEntry("hello", "world");

        String serializedKVEntry = kvEntrySerializer.serialize(kvEntry).toString();
        KVEntry deserializedKVEntry = kvEntryDeserializer.deserialize(serializedKVEntry);

        Assert.assertEquals(kvEntry.toString(), deserializedKVEntry.toString());
        Assert.assertEquals(kvEntry, deserializedKVEntry);
    }
}
