package testing.weloveclouds.kvstore.serialization;

import java.net.UnknownHostException;

import org.junit.Test;

import junit.framework.Assert;
import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.kvstore.deserialization.helper.IDeserializer;
import weloveclouds.kvstore.deserialization.helper.ServerConnectionInfoDeserializer;
import weloveclouds.kvstore.serialization.exceptions.DeserializationException;
import weloveclouds.kvstore.serialization.helper.ISerializer;
import weloveclouds.kvstore.serialization.helper.ServerConnectionInfoSerializer;

public class ServerConnectionInfoTest {

    public static final IDeserializer<ServerConnectionInfo, String> deserializer = new ServerConnectionInfoDeserializer();
    public static final ISerializer<String, ServerConnectionInfo> serializer = new ServerConnectionInfoSerializer();

    @Test
    public void test() throws DeserializationException, UnknownHostException {
        ServerConnectionInfo info = new ServerConnectionInfo.ServerConnectionInfoBuilder().ipAddress("localhost").port(8080).build();

        String ser = serializer.serialize(info);
        ServerConnectionInfo deser = deserializer.deserialize(ser);

        Assert.assertEquals(info.toString(), deser.toString());
        Assert.assertEquals(info, deser);
    }
    
}
