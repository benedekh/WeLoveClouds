package weloveclouds.kvstore.serialization.helper;

import weloveclouds.communication.models.ServerConnectionInfo;

/**
 * A serializer which converts a {@link ServerConnectionInfo} to a {@link String}.
 * 
 * @author Benedek
 */
public class ServerConnectionInfoSerializer implements ISerializer<String, ServerConnectionInfo> {

    public static final String SEPARATOR = "-\t-";

    @Override
    public String serialize(ServerConnectionInfo target) {
        String serialized = null;

        if (target != null) {
            serialized = target.toStringWithDelimiter(SEPARATOR);
        }

        return serialized;
    }

}
