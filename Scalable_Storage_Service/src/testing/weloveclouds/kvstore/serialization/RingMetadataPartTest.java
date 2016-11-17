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

public class RingMetadataPartTest {

    public static final IDeserializer<RingMetadataPart, String> deserializer =
            new RingMetadataPartDeserializer();
    public static final ISerializer<String, RingMetadataPart> serializer =
            new RingMetadataPartSerializer();

    @Test
    public void test() throws DeserializationException, UnknownHostException {
        ServerConnectionInfo sci = new ServerConnectionInfo.ServerConnectionInfoBuilder()
                .ipAddress("localhost").port(8080).build();
        RingMetadataPart metadataPart = new RingMetadataPart.RingMetadataPartBuilder()
                .connectionInfo(sci).range(new HashRange(Hash.MIN_VALUE, Hash.MAX_VALUE)).build();

        String ser = serializer.serialize(metadataPart);
        RingMetadataPart deser = deserializer.deserialize(ser);

        Assert.assertEquals(metadataPart.toString(), deser.toString());
        Assert.assertEquals(metadataPart, deser);
    }
}
