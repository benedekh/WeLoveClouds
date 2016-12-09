package weloveclouds.kvstore.deserialization.helper;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.communication.models.ServerConnectionInfos;
import weloveclouds.kvstore.serialization.exceptions.DeserializationException;
import weloveclouds.kvstore.serialization.helper.ServerConnectionInfosSerializer;

/**
 * A deserializer which converts a {@link ServerConnectionInfos} to a {@link String}.
 * 
 * @author Benedek
 */
public class ServerConnectionInfosDeserializer
        implements IDeserializer<ServerConnectionInfos, String> {

    private static final Logger LOGGER = Logger.getLogger(ServerConnectionInfosDeserializer.class);

    private IDeserializer<ServerConnectionInfo, String> connectionInfoDeserializer =
            new ServerConnectionInfoDeserializer();

    @Override
    public ServerConnectionInfos deserialize(String from) throws DeserializationException {
        ServerConnectionInfos deserialized = null;

        if (from != null && !"null".equals(from)) {
            LOGGER.debug("Deserializing a ServerConnectionInfos from String.");
            // raw message split
            String[] rawStorageUnits = from.split(ServerConnectionInfosSerializer.SEPARATOR);

            // raw fields
            Set<ServerConnectionInfo> deserializedConnectionInfos = new HashSet<>();
            for (String rawStorageUnit : rawStorageUnits) {
                // deserialized fields
                deserializedConnectionInfos
                        .add(connectionInfoDeserializer.deserialize(rawStorageUnit));
            }

            // deserialized object
            deserialized = new ServerConnectionInfos(deserializedConnectionInfos);
            LOGGER.debug("Deserializing a ServerConnectionInfos from String finished.");
        }

        return deserialized;
    }

}
