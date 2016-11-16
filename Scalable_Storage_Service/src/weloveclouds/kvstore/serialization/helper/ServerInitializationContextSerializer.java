package weloveclouds.kvstore.serialization.helper;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.server.models.ServerInitializationContext;

public class ServerInitializationContextSerializer
        implements ISerializer<String, ServerInitializationContext> {

    public static final String SEPARATOR = "-\r-";

    @Override
    public String serialize(ServerInitializationContext target) {
        String serialized = null;

        if (target != null) {
            serialized = CustomStringJoiner.join(SEPARATOR, target.getRingMetadata().toString(),
                    String.valueOf(target.getCacheSize()), target.getDisplacementStrategyName());
        }

        return serialized;
    }

}
