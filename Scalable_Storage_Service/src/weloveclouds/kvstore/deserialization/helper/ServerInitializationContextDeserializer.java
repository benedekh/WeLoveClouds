package weloveclouds.kvstore.deserialization.helper;

import static weloveclouds.client.utils.CustomStringJoiner.join;

import org.apache.log4j.Logger;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.hashing.models.RingMetadata;
import weloveclouds.kvstore.serialization.exceptions.DeserializationException;
import weloveclouds.kvstore.serialization.helper.ServerInitializationContextSerializer;
import weloveclouds.server.models.ServerInitializationContext;

public class ServerInitializationContextDeserializer
        implements IDeserializer<ServerInitializationContext, String> {

    private static final int NUMBER_OF_INITIALIZATION_INFO_PARTS = 3;

    private static final int RING_METADATA_INDEX = 0;
    private static final int CACHE_SIZE_INDEX = 1;
    private static final int DISPLACEMENT_STARTEGY_INDEX = 2;

    private IDeserializer<RingMetadata, String> ringMetadataDeserializer =
            new RingMetadataDeserializer();

    private Logger logger = Logger.getLogger(getClass());

    @Override
    public ServerInitializationContext deserialize(String from) throws DeserializationException {
        ServerInitializationContext deserialized = null;

        if (from != null && !"null".equals(from)) {
            // raw message split
            String[] parts = from.split(ServerInitializationContextSerializer.SEPARATOR);

            // length check
            if (parts.length != NUMBER_OF_INITIALIZATION_INFO_PARTS) {
                String errorMessage =
                        CustomStringJoiner.join("", "Initialization info must consist of exactly ",
                                String.valueOf(NUMBER_OF_INITIALIZATION_INFO_PARTS), " parts.");
                logger.debug(errorMessage);
                throw new DeserializationException(errorMessage);
            }

            // raw fields
            String ringMetadataStr = parts[RING_METADATA_INDEX];
            String cacheSizeStr = parts[CACHE_SIZE_INDEX];
            String displacementStrategy = parts[DISPLACEMENT_STARTEGY_INDEX];

            try {
                // deserialized fields
                RingMetadata ringMetadata = ringMetadataDeserializer.deserialize(ringMetadataStr);
                int cacheSize = Integer.valueOf(cacheSizeStr);

                // deserialized object
                deserialized = new ServerInitializationContext(ringMetadata, cacheSize,
                        displacementStrategy);
                logger.debug(join(" ", "Deserialized server initialization info is:",
                        deserialized.toString()));
            } catch (NumberFormatException ex) {
                String errorMessage =
                        CustomStringJoiner.join(": ", "Cache size is NaN", parts[CACHE_SIZE_INDEX]);
                logger.error(errorMessage);
                throw new DeserializationException(errorMessage);
            }
        }

        return deserialized;
    }

}
