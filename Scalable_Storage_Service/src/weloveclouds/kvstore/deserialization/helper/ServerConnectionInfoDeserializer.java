package weloveclouds.kvstore.deserialization.helper;

import static weloveclouds.client.utils.CustomStringJoiner.join;

import java.net.UnknownHostException;

import org.apache.log4j.Logger;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.kvstore.serialization.exceptions.DeserializationException;
import weloveclouds.kvstore.serialization.helper.ServerConnectionInfoSerializer;

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
            // raw message split
            String[] parts = from.split(ServerConnectionInfoSerializer.SEPARATOR);

            // length check
            if (parts.length != NUMBER_OF_CONNECTION_INFO_PARTS) {
                String errorMessage =
                        CustomStringJoiner.join("", "Connection info must consist of exactly ",
                                String.valueOf(NUMBER_OF_CONNECTION_INFO_PARTS), " parts.");
                LOGGER.debug(errorMessage);
                throw new DeserializationException(errorMessage);
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
                LOGGER.debug(
                        join(" ", "Deserialized connection info is:", deserialized.toString()));
            } catch (NumberFormatException ex) {
                String errorMessage =
                        CustomStringJoiner.join(": ", "Port is NaN", parts[PORT_INDEX]);
                LOGGER.error(errorMessage);
                throw new DeserializationException(errorMessage);
            } catch (UnknownHostException ex) {
                String errorMessage = CustomStringJoiner.join(": ",
                        "Host referred by IP address is unknown", ipAddress);
                LOGGER.error(errorMessage);
                throw new DeserializationException(errorMessage);
            }
        }
        return deserialized;
    }

}
