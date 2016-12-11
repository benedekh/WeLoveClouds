package weloveclouds.kvstore.deserialization.helper;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.kvstore.serialization.exceptions.DeserializationException;
import weloveclouds.kvstore.serialization.helper.SetOfServerConnectionInfosSerializer;

/**
 * A deserializer which converts a {@link Set<ServerConnectionInfo>} to a {@link String}.
 * 
 * @author Benedek
 */
public class SetOfServerConnectionInfosDeserializer
        implements IDeserializer<Set<ServerConnectionInfo>, String> {

    private static final Logger LOGGER =
            Logger.getLogger(SetOfServerConnectionInfosDeserializer.class);

    private IDeserializer<ServerConnectionInfo, String> connectionInfoDeserializer =
            new ServerConnectionInfoDeserializer();

    @Override
    public Set<ServerConnectionInfo> deserialize(String from) throws DeserializationException {
        Set<ServerConnectionInfo> deserialized = null;

        if (from != null && !"null".equals(from)) {
            LOGGER.debug("Deserializing a Set<ServerConnectionInfo> from String.");
            // raw message split
            String[] rawStorageUnits = from.split(SetOfServerConnectionInfosSerializer.SEPARATOR);

            // deserialized object
            deserialized = new HashSet<>();
            for (String rawStorageUnit : rawStorageUnits) {
                // deserialized fields
                deserialized.add(connectionInfoDeserializer.deserialize(rawStorageUnit));
            }
            LOGGER.debug("Deserializing a Set<ServerConnectionInfo> from String finished.");
        }

        return deserialized;
    }

}
