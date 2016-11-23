package testing.weloveclouds.kvstore.serialization;

import java.net.UnknownHostException;

import org.junit.Test;

import junit.framework.Assert;
import junit.framework.TestCase;
import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.kvstore.deserialization.helper.IDeserializer;
import weloveclouds.kvstore.deserialization.helper.ServerConnectionInfoDeserializer;
import weloveclouds.kvstore.serialization.exceptions.DeserializationException;
import weloveclouds.kvstore.serialization.helper.ISerializer;
import weloveclouds.kvstore.serialization.helper.ServerConnectionInfoSerializer;

/**
 * Tests for the {@link ServerConnectionInfo} to verify its serialization and deserialization
 * processes.
 * 
 * @author Benedek
 */
public class ServerConnectionInfoTest extends TestCase {

    private static final IDeserializer<ServerConnectionInfo, String> connectionInfoDeserializer =
            new ServerConnectionInfoDeserializer();
    private static final ISerializer<String, ServerConnectionInfo> connectionInfoSerializer =
            new ServerConnectionInfoSerializer();

    @Test
    public void testServerConnectionInfoSerializationAndDeserialization()
            throws DeserializationException, UnknownHostException {
        ServerConnectionInfo connectionInfo =
                new ServerConnectionInfo.Builder().ipAddress("localhost").port(8080).build();

        String serializedConnectionInfo = connectionInfoSerializer.serialize(connectionInfo);
        ServerConnectionInfo deserializedConnectionInfo =
                connectionInfoDeserializer.deserialize(serializedConnectionInfo);

        Assert.assertEquals(connectionInfo.toString(), deserializedConnectionInfo.toString());
        Assert.assertEquals(connectionInfo, deserializedConnectionInfo);
    }

}
