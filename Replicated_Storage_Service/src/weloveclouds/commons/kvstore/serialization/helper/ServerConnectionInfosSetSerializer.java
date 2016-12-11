package weloveclouds.commons.kvstore.serialization.helper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.communication.models.ServerConnectionInfo;

/**
 * A serializer which converts a {@link Set<ServerConnectionInfo>} to a {@link String}.
 * 
 * @author Benedek
 */
public class ServerConnectionInfosSetSerializer
        implements ISerializer<String, Set<ServerConnectionInfo>> {

    public static final String SEPARATOR = "-Ł-";
    private static final Logger LOGGER = Logger.getLogger(ServerConnectionInfosSetSerializer.class);

    private ISerializer<String, ServerConnectionInfo> serverConnectionInfoSerializer =
            new ServerConnectionInfoSerializer();

    @Override
    public String serialize(Set<ServerConnectionInfo> target) {
        String serialized = null;

        if (target != null) {
            LOGGER.debug("Serializing a Set<ServerConnectionInfo>.");
            // string representation
            Set<String> connectionInfoStrs = new HashSet<>();
            for (ServerConnectionInfo connectionInfo : target) {
                connectionInfoStrs.add(serverConnectionInfoSerializer.serialize(connectionInfo));
            }

            // merged string representation
            serialized = CustomStringJoiner.join(SEPARATOR, new ArrayList<>(connectionInfoStrs));
            LOGGER.debug("Serializing a Set<ServerConnectionInfo> finished.");
        }

        return serialized;
    }

}
