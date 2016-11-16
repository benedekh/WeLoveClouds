package weloveclouds.kvstore.serialization.helper;

import weloveclouds.hashing.models.RingMetadata;;

public class RingMetadataSerializer implements ISerializer<String, RingMetadata> {

    public static final String SEPARATOR = "-\r-";

    @Override
    public String serialize(RingMetadata target) {
        String serialized = null;

        if (target != null) {
            serialized = target.toStringWithDelimiter(SEPARATOR);
        }

        return serialized;
    }

}
