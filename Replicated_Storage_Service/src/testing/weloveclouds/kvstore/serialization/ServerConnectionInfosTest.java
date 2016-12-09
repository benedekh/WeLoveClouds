package testing.weloveclouds.kvstore.serialization;

import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashSet;

import org.junit.Test;

import junit.framework.Assert;
import junit.framework.TestCase;
import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.communication.models.ServerConnectionInfos;
import weloveclouds.kvstore.deserialization.helper.IDeserializer;
import weloveclouds.kvstore.deserialization.helper.ServerConnectionInfosDeserializer;
import weloveclouds.kvstore.serialization.exceptions.DeserializationException;
import weloveclouds.kvstore.serialization.helper.ISerializer;
import weloveclouds.kvstore.serialization.helper.ServerConnectionInfosSerializer;

/**
 * Tests for the {@link ServerConnectionInfos} to verify its serialization and deserialization
 * processes.
 * 
 * @author Benedek
 */
public class ServerConnectionInfosTest extends TestCase {

    private static final IDeserializer<ServerConnectionInfos, String> connectionInfosDeserializer =
            new ServerConnectionInfosDeserializer();
    private static final ISerializer<String, ServerConnectionInfos> connectionInfosSerializer =
            new ServerConnectionInfosSerializer();

    @Test
    public void testServerConnectionInfoSerializationAndDeserialization()
            throws DeserializationException, UnknownHostException {
        ServerConnectionInfo connectionInfo1 =
                new ServerConnectionInfo.Builder().ipAddress("localhost").port(8080).build();
        ServerConnectionInfo connectionInfo2 =
                new ServerConnectionInfo.Builder().ipAddress("localhost").port(8081).build();

        ServerConnectionInfos connectionInfos = new ServerConnectionInfos(
                new HashSet<>(Arrays.asList(connectionInfo1, connectionInfo2)));

        String serializedConnectionInfos = connectionInfosSerializer.serialize(connectionInfos);
        ServerConnectionInfos deserializedConnectionInfos =
                connectionInfosDeserializer.deserialize(serializedConnectionInfos);

        Assert.assertEquals(connectionInfos.toString(), deserializedConnectionInfos.toString());
        Assert.assertEquals(connectionInfos, deserializedConnectionInfos);
    }

}
