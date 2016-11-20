package testing.weloveclouds.kvstore.serialization;

import java.net.UnknownHostException;

import org.junit.Test;

import junit.framework.Assert;
import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.hashing.models.Hash;
import weloveclouds.hashing.models.HashRange;
import weloveclouds.hashing.models.RingMetadataPart;
import weloveclouds.kvstore.deserialization.helper.IDeserializer;
import weloveclouds.kvstore.deserialization.helper.RingMetadataPartDeserializer;
import weloveclouds.kvstore.serialization.exceptions.DeserializationException;
import weloveclouds.kvstore.serialization.helper.ISerializer;
import weloveclouds.kvstore.serialization.helper.RingMetadataPartSerializer;

/**
 * Tests for the {@link RingMetadataPart} to verify its serialization and deserialization
 * processes.
 * 
 * @author Benedek
 */
public class RingMetadataPartTest {

    private static final IDeserializer<RingMetadataPart, String> metadataPartDeserializer =
            new RingMetadataPartDeserializer();
    private static final ISerializer<String, RingMetadataPart> metadataPartSerializer =
            new RingMetadataPartSerializer();

    @Test
    public void testRingMetadataPartSerializationAndDeserialization()
            throws DeserializationException, UnknownHostException {
        ServerConnectionInfo sci =
                new ServerConnectionInfo.Builder().ipAddress("localhost").port(8080).build();
        RingMetadataPart metadataPart = new RingMetadataPart.Builder().connectionInfo(sci)
                .range(new HashRange.Builder().begin(Hash.MIN_VALUE).end(Hash.MAX_VALUE).build())
                .build();

        String serializedMetadataPart = metadataPartSerializer.serialize(metadataPart);
        RingMetadataPart deserializedMetadataPart =
                metadataPartDeserializer.deserialize(serializedMetadataPart);

        Assert.assertEquals(metadataPart.toString(), deserializedMetadataPart.toString());
        Assert.assertEquals(metadataPart, deserializedMetadataPart);
    }
}
