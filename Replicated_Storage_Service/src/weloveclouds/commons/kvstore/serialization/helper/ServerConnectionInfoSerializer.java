package weloveclouds.commons.kvstore.serialization.helper;

import java.net.InetAddress;

import org.apache.log4j.Logger;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.communication.models.ServerConnectionInfo;

/**
 * A serializer which converts a {@link ServerConnectionInfo} to a {@link String}.
 * 
 * @author Benedek
 */
public class ServerConnectionInfoSerializer implements ISerializer<String, ServerConnectionInfo> {

    public static final String SEPARATOR = "-ł-";
    private static final Logger LOGGER = Logger.getLogger(ServerConnectionInfo.class);

    @Override
    public String serialize(ServerConnectionInfo target) {
        String serialized = null;

        if (target != null) {
            LOGGER.debug("Serializing a ServerConnectionInfo.");
            // original fields
            InetAddress ipAddress = target.getIpAddress();
            int port = target.getPort();

            // string representation
            String ipAddressStr = ipAddress.getHostAddress();
            String portStr = String.valueOf(port);

            // merged string representation
            serialized = CustomStringJoiner.join(SEPARATOR, ipAddressStr, portStr);
            LOGGER.debug("Serializing a ServerConnectionInfo finished.");
        }

        return serialized;
    }

}
