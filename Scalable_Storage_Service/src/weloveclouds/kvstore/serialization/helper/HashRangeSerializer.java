package weloveclouds.kvstore.serialization.helper;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.hashing.models.HashRange;

public class HashRangeSerializer implements ISerializer<String, HashRange> {

    public static final String SEPARATOR = "-\r-";

    @Override
    public String serialize(HashRange target) {
        String serialized = null;

        if (target != null) {
            serialized = CustomStringJoiner.join(SEPARATOR, target.getStart().toString(),
                    target.getEnd().toString());
        }

        return serialized;
    }

}
