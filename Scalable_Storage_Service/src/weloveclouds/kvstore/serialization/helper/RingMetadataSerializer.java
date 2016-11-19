package weloveclouds.kvstore.serialization.helper;

import weloveclouds.hashing.models.RingMetadata;
import weloveclouds.hashing.models.RingMetadataPart;;

/**
 * A serializer which converts a {@link RingMetadata} to a {@link String}.
 * 
 * @author Benedek
 */
public class RingMetadataSerializer implements ISerializer<String, RingMetadata> {

    public static final String SEPARATOR = "-\r\r-";

    private ISerializer<String, RingMetadataPart> metadataPartSerializer =
            new RingMetadataPartSerializer();

    @Override
    public String serialize(RingMetadata target) {
        String serialized = null;

        if (target != null) {
            serialized = target.toStringWithDelimiter(SEPARATOR, metadataPartSerializer);
        }

        return serialized;
    }

}
