package weloveclouds.kvstore.serialization.helper;

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

    @Override
    public String serialize(ServerInitializationContext target) {
        String serialized = null;

        if (target != null) {
            serialized = CustomStringJoiner.join(SEPARATOR, String.valueOf(target.getCacheSize()),
                    target.getDisplacementStrategyName());
        }

        return serialized;
    }

}
