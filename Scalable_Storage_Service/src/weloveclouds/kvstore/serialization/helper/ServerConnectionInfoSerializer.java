package weloveclouds.kvstore.serialization.helper;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.communication.models.ServerConnectionInfo;

public class ServerConnectionInfoSerializer implements ISerializer<String, ServerConnectionInfo> {

    public static final String SEPARATOR = "-\r-";

    @Override
    public String serialize(ServerConnectionInfo target) {
        String serialized = null;

        if (target != null) {
            serialized = CustomStringJoiner.join(SEPARATOR, target.getIpAddress().getHostAddress(),
                    String.valueOf(target.getPort()));
        }

        return serialized;
    }

}
