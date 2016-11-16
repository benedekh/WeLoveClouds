package weloveclouds.kvstore.serialization.helper;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.hashing.models.RingMetadataPart;

public class RingMetadataPartSerializer implements ISerializer<String, RingMetadataPart> {

    public static final String SEPARATOR = "-\t-";

    @Override
    public String serialize(RingMetadataPart target) {
        String serialized = null;

        if (target != null) {
            serialized = CustomStringJoiner.join(SEPARATOR, target.getConnectionInfo().toString(),
                    target.getRange().toString());
        }

        return serialized;
    }

}
