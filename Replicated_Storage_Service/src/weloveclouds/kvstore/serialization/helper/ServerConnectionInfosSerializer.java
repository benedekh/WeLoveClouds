package weloveclouds.kvstore.serialization.helper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.communication.models.ServerConnectionInfos;

/**
 * A serializer which converts a {@link ServerConnectionInfos} to a {@link String}.
 * 
 * @author Benedek
 */
public class ServerConnectionInfosSerializer implements ISerializer<String, ServerConnectionInfos> {

    public static final String SEPARATOR = "-Ł-";
    private static final Logger LOGGER = Logger.getLogger(ServerConnectionInfosSerializer.class);

    private ISerializer<String, ServerConnectionInfo> serverConnectionInfoSerializer =
            new ServerConnectionInfoSerializer();

    @Override
    public String serialize(ServerConnectionInfos target) {
        String serialized = null;

        if (target != null) {
            LOGGER.debug("Serializing a ServerConnectionInfos.");
            // original fields
            Set<ServerConnectionInfo> connectionInfos = target.getServerConnectionInfos();

            // string representation
            Set<String> connectionInfoStrs = new HashSet<>();
            for (ServerConnectionInfo connectionInfo : connectionInfos) {
                connectionInfoStrs.add(serverConnectionInfoSerializer.serialize(connectionInfo));
            }

            // merged string representation
            serialized = CustomStringJoiner.join(SEPARATOR, new ArrayList<>(connectionInfoStrs));
            LOGGER.debug("Serializing a ServerConnectionInfos finished.");
        }

        return serialized;
    }

}
