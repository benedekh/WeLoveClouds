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

    private static final IDeserializer<ServerConnectionInfo, String> connectionInfoDeserializer =
            new ServerConnectionInfoDeserializer();
    private static final ISerializer<String, ServerConnectionInfo> connectionInfoSerializer =
            new ServerConnectionInfoSerializer();

    @Test
    public void testServerConnectionInfoSerializationAndDeserialization()
            throws DeserializationException, UnknownHostException {
        ServerConnectionInfo connectionInfo = new ServerConnectionInfo.ServerConnectionInfoBuilder()
                .ipAddress("localhost").port(8080).build();

        String serializedConnectionInfo = connectionInfoSerializer.serialize(connectionInfo);
        ServerConnectionInfo deserializedConnectionInfo =
                connectionInfoDeserializer.deserialize(serializedConnectionInfo);

        Assert.assertEquals(connectionInfo.toString(), deserializedConnectionInfo.toString());
        Assert.assertEquals(connectionInfo, deserializedConnectionInfo);
    }

}
