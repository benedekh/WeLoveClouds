package weloveclouds.kvstore.deserialization.helper;

import java.net.UnknownHostException;

import org.apache.log4j.Logger;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.kvstore.serialization.exceptions.DeserializationException;
import weloveclouds.kvstore.serialization.helper.ServerConnectionInfoSerializer;

/**
 * A deserializer which converts a {@link ServerConnectionInfo} to a {@link String}.
 * 
 * @author Benedek
 */
public class ServerConnectionInfoDeserializer
        implements IDeserializer<ServerConnectionInfo, String> {

    private static final int NUMBER_OF_CONNECTION_INFO_PARTS = 2;

    private static final int IP_ADDRESS_INDEX = 0;
    private static final int PORT_INDEX = 1;

    private static final Logger LOGGER = Logger.getLogger(ServerConnectionInfoDeserializer.class);

    @Override
    public ServerConnectionInfo deserialize(String from) throws DeserializationException {
        ServerConnectionInfo deserialized = null;

        if (from != null && !"null".equals(from)) {
            LOGGER.debug("Deserializing a ServerConnectionInfo from String.");
            // raw message split
            String[] parts = from.split(ServerConnectionInfoSerializer.SEPARATOR);

            // length check
            if (parts.length != NUMBER_OF_CONNECTION_INFO_PARTS) {
                throw new DeserializationException(
                        CustomStringJoiner.join("", "Connection info must consist of exactly ",
                                String.valueOf(NUMBER_OF_CONNECTION_INFO_PARTS), " parts."));
            }

            // raw fields
            String ipAddress = parts[IP_ADDRESS_INDEX];
            String portStr = parts[PORT_INDEX];

            try {
                // deserialized fields
                int port = Integer.valueOf(portStr);

                // deserialized object
                deserialized =
                        new ServerConnectionInfo.Builder().ipAddress(ipAddress).port(port).build();
                LOGGER.debug("Deserializing a ServerConnectionInfo from String finished.");
            } catch (NumberFormatException ex) {
                throw new DeserializationException(
                        CustomStringJoiner.join(": ", "Port is NaN", parts[PORT_INDEX]));
            } catch (UnknownHostException ex) {
                throw new DeserializationException(CustomStringJoiner.join(": ",
                        "Host referred by IP address is unknown", ipAddress));
            }
        }
        return deserialized;
    }

}
