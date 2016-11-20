package weloveclouds.kvstore.serialization.helper;

import weloveclouds.communication.models.ServerConnectionInfo;

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
