package weloveclouds.kvstore.serialization.helper;

import org.apache.log4j.Logger;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.server.models.ServerInitializationContext;

/**
 * A serializer which converts a {@link ServerInitializationContext} to a {@link String}.
 * 
 * @author Benedek
 */
public class ServerInitializationContextSerializer
        implements ISerializer<String, ServerInitializationContext> {

    public static final String SEPARATOR = "-\t\r-";
    private static final Logger LOGGER = Logger.getLogger(ServerInitializationContext.class);

    @Override
    public String serialize(ServerInitializationContext target) {
        String serialized = null;

        if (target != null) {
            LOGGER.debug("Serializing a ServerInitializationContext.");
            serialized = CustomStringJoiner.join(SEPARATOR, String.valueOf(target.getCacheSize()),
                    target.getDisplacementStrategyName());
            LOGGER.debug("Serializing a ServerInitializationContext finished.");
        }

        return serialized;
    }

}
