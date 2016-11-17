package weloveclouds.kvstore.serialization.helper;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.hashing.models.RingMetadata;
import weloveclouds.server.models.ServerInitializationContext;

public class ServerInitializationContextSerializer
        implements ISerializer<String, ServerInitializationContext> {

    public static final String SEPARATOR = "-\t\r-";

    private ISerializer<String, RingMetadata> ringMetadataSerializer = new RingMetadataSerializer();

    @Override
    public String serialize(ServerInitializationContext target) {
        String serialized = null;

        if (target != null) {
            serialized = CustomStringJoiner.join(SEPARATOR,
                    ringMetadataSerializer.serialize(target.getRingMetadata()),
                    String.valueOf(target.getCacheSize()), target.getDisplacementStrategyName());
        }

        return serialized;
    }

}
