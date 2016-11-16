package weloveclouds.kvstore.serialization.helper;

import weloveclouds.hashing.models.RingMetadataPart;

public class RingMetadataPartSerializer implements ISerializer<String, RingMetadataPart> {

    public static final String SEPARATOR = "-\t-";

    @Override
    public String serialize(RingMetadataPart target) {
        String serialized = null;

        if (target != null) {
            serialized = target.toStringWithDelimiter(SEPARATOR);
        }

        return serialized;
    }

}
