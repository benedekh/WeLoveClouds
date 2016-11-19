package weloveclouds.kvstore.deserialization.helper;

import static weloveclouds.client.utils.CustomStringJoiner.join;

import org.apache.log4j.Logger;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.kvstore.serialization.exceptions.DeserializationException;
import weloveclouds.kvstore.serialization.helper.ServerInitializationContextSerializer;
import weloveclouds.server.models.ServerInitializationContext;

/**
 * A deserializer which converts a {@link ServerInitializationContext} to a {@link String}.
 * 
 * @author Benedek
 */
public class ServerInitializationContextDeserializer
        implements IDeserializer<ServerInitializationContext, String> {

    private static final int NUMBER_OF_INITIALIZATION_INFO_PARTS = 2;

    private static final int CACHE_SIZE_INDEX = 0;
    private static final int DISPLACEMENT_STARTEGY_INDEX = 1;

    private static final Logger LOGGER =
            Logger.getLogger(ServerInitializationContextDeserializer.class);

    @Override
    public ServerInitializationContext deserialize(String from) throws DeserializationException {
        ServerInitializationContext deserialized = null;

        if (from != null && !"null".equals(from)) {
            LOGGER.debug("Deserializing a ServerInitializationContext from String.");
            // raw message split
            String[] parts = from.split(ServerInitializationContextSerializer.SEPARATOR);

            // length check
            if (parts.length != NUMBER_OF_INITIALIZATION_INFO_PARTS) {
                String errorMessage =
                        CustomStringJoiner.join("", "Initialization info must consist of exactly ",
                                String.valueOf(NUMBER_OF_INITIALIZATION_INFO_PARTS), " parts.");
                LOGGER.debug(errorMessage);
                throw new DeserializationException(errorMessage);
            }

            // raw fields
            String cacheSizeStr = parts[CACHE_SIZE_INDEX];
            String displacementStrategyStr = parts[DISPLACEMENT_STARTEGY_INDEX];

            try {
                // deserialized fields
                int cacheSize = Integer.valueOf(cacheSizeStr);
                String displacementStrategy =
                        "null".equals(displacementStrategyStr) ? null : displacementStrategyStr;

                // deserialized object
                deserialized = new ServerInitializationContext.Builder().cacheSize(cacheSize)
                        .displacementStrategyName(displacementStrategy).build();
                LOGGER.debug(join(" ", "Deserialized server initialization info is:",
                        deserialized.toString()));
            } catch (NumberFormatException ex) {
                String errorMessage =
                        CustomStringJoiner.join(": ", "Cache size is NaN", parts[CACHE_SIZE_INDEX]);
                LOGGER.error(errorMessage);
                throw new DeserializationException(errorMessage);
            }
        }

        return deserialized;
    }

}
