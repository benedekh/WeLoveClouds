package testing.weloveclouds.kvstore.serialization;

import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import junit.framework.Assert;
import junit.framework.TestCase;
import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.kvstore.deserialization.helper.IDeserializer;
import weloveclouds.kvstore.deserialization.helper.ServerConnectionInfosSetDeserializer;
import weloveclouds.kvstore.serialization.exceptions.DeserializationException;
import weloveclouds.kvstore.serialization.helper.ISerializer;
import weloveclouds.kvstore.serialization.helper.ServerConnectionInfosSetSerializer;
import weloveclouds.server.utils.SetToStringUtility;

/**
 * Tests for the {@link Set<ServerConnectionInfo>} to verify its serialization and deserialization
 * processes.
 * 
 * @author Benedek
 */
public class ServerConnectionInfosSetTest extends TestCase {

    private static final IDeserializer<Set<ServerConnectionInfo>, String> connectionInfosDeserializer =
            new ServerConnectionInfosSetDeserializer();
    private static final ISerializer<String, Set<ServerConnectionInfo>> connectionInfosSerializer =
            new ServerConnectionInfosSetSerializer();

    @Test
    public void testServerConnectionInfoSerializationAndDeserialization()
            throws DeserializationException, UnknownHostException {
        ServerConnectionInfo connectionInfo1 =
                new ServerConnectionInfo.Builder().ipAddress("localhost").port(8080).build();
        ServerConnectionInfo connectionInfo2 =
                new ServerConnectionInfo.Builder().ipAddress("localhost").port(8081).build();

        Set<ServerConnectionInfo> connectionInfos =
                new HashSet<>(Arrays.asList(connectionInfo1, connectionInfo2));

        String serializedConnectionInfos = connectionInfosSerializer.serialize(connectionInfos);
        Set<ServerConnectionInfo> deserializedConnectionInfos =
                connectionInfosDeserializer.deserialize(serializedConnectionInfos);

        Assert.assertEquals(SetToStringUtility.toString(connectionInfos),
                SetToStringUtility.toString(deserializedConnectionInfos));
        Assert.assertEquals(connectionInfos, deserializedConnectionInfos);
    }

}
