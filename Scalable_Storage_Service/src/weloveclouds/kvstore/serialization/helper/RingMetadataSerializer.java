package weloveclouds.kvstore.serialization.helper;

import weloveclouds.hashing.models.RingMetadataPart;
import weloveclouds.hashing.models.RingMetadata;;

public class RingMetadataSerializer implements ISerializer<String, RingMetadata> {

    public static final String SEPARATOR = "-\r-";

    @Override
    public String serialize(RingMetadata target) {
        String serialized = null;

        if (target != null) {
            StringBuilder sb = new StringBuilder();
            for (RingMetadataPart info : target.getMetadataParts()) {
                sb.append(info);
                sb.append(SEPARATOR);
            }
            sb.setLength(sb.length() - SEPARATOR.length());
            serialized = sb.toString();
        }

        return serialized;
    }

}
