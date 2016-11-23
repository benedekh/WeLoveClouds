package weloveclouds.kvstore.serialization.helper;

import org.apache.log4j.Logger;

import weloveclouds.communication.models.ServerConnectionInfo;

/**
 * A serializer which converts a {@link ServerConnectionInfo} to a {@link String}.
 * 
 * @author Benedek
 */
public class ServerConnectionInfoSerializer implements ISerializer<String, ServerConnectionInfo> {

    public static final String SEPARATOR = "-\t-";
    private static final Logger LOGGER = Logger.getLogger(ServerConnectionInfo.class);

    @Override
    public String serialize(ServerConnectionInfo target) {
        String serialized = null;

        if (target != null) {
            LOGGER.debug("Serializing a ServerConnectionInfo.");
            serialized = target.toStringWithDelimiter(SEPARATOR);
            LOGGER.debug("Serializing a ServerConnectionInfo finished.");
        }

        return serialized;
    }

}
