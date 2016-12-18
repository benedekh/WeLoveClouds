package testing.weloveclouds.kvstore.serialization;

import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import junit.framework.Assert;
import junit.framework.TestCase;
import weloveclouds.commons.kvstore.deserialization.exceptions.DeserializationException;
import weloveclouds.commons.kvstore.deserialization.helper.ServerConnectionInfosSetDeserializer;
import weloveclouds.commons.kvstore.serialization.helper.ServerConnectionInfosIterableSerializer;
import weloveclouds.commons.serialization.IDeserializer;
import weloveclouds.commons.serialization.ISerializer;
import weloveclouds.commons.serialization.models.AbstractXMLNode;
import weloveclouds.commons.utils.StringUtils;
import weloveclouds.communication.models.ServerConnectionInfo;

/**
 * Tests for the {@link Set<ServerConnectionInfo>} to verify its serialization and deserialization
 * processes.
 * 
 * @author Benedek
 */
public class ServerConnectionInfosSetTest extends TestCase {

    private static final IDeserializer<Set<ServerConnectionInfo>, String> connectionInfosDeserializer =
            new ServerConnectionInfosSetDeserializer();
    private static final ISerializer<AbstractXMLNode, Iterable<ServerConnectionInfo>> connectionInfosSerializer =
            new ServerConnectionInfosIterableSerializer();

    @Test
    public void testServerConnectionInfoSerializationAndDeserialization()
            throws DeserializationException, UnknownHostException {
        ServerConnectionInfo connectionInfo1 =
                new ServerConnectionInfo.Builder().ipAddress("localhost").port(8080).build();
        ServerConnectionInfo connectionInfo2 =
                new ServerConnectionInfo.Builder().ipAddress("localhost").port(8081).build();

        Set<ServerConnectionInfo> connectionInfos =
                new HashSet<>(Arrays.asList(connectionInfo1, connectionInfo2));

        String serializedConnectionInfos =
                connectionInfosSerializer.serialize(connectionInfos).toString();
        Set<ServerConnectionInfo> deserializedConnectionInfos =
                connectionInfosDeserializer.deserialize(serializedConnectionInfos);

        Assert.assertEquals(StringUtils.setToString(connectionInfos),
                StringUtils.setToString(deserializedConnectionInfos));
        Assert.assertEquals(connectionInfos, deserializedConnectionInfos);
    }

}
