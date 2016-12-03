package weloveclouds.kvstore.serialization.helper;

import org.apache.log4j.Logger;

import weloveclouds.commons.hashing.models.RingMetadata;
import weloveclouds.commons.hashing.models.RingMetadataPart;;

/**
 * A serializer which converts a {@link RingMetadata} to a {@link String}.
 * 
 * @author Benedek
 */
public class RingMetadataSerializer implements ISerializer<String, RingMetadata> {

    public static final String SEPARATOR = "-łł-";
    private static final Logger LOGGER = Logger.getLogger(RingMetadata.class);

    private ISerializer<String, RingMetadataPart> metadataPartSerializer =
            new RingMetadataPartSerializer();

    @Override
    public String serialize(RingMetadata target) {
        String serialized = null;

        if (target != null) {
            LOGGER.debug("Serializing a RingMetadata.");
            serialized = target.toStringWithDelimiter(SEPARATOR, metadataPartSerializer);
            LOGGER.debug("Serializing a RingMetadata finished.");
        }

        return serialized;
    }

}
